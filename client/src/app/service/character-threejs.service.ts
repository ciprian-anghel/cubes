import { Injectable, ElementRef, OnDestroy, inject, DestroyRef } from '@angular/core';
import { Scene, PerspectiveCamera, WebGLRenderer, AmbientLight, DirectionalLight, CubeTextureLoader, WebGLRendererParameters, TextureLoader, Mesh, MeshStandardMaterial, RepeatWrapping, Group, Object3DEventMap } from 'three';
import { GLTF, GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import WebGL from 'three/examples/jsm/capabilities/WebGL.js';
import { SharedService } from './shared.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BackendCommunicationService } from '../api/service/backend-communication/backend-communication.service';
import { Option } from '../model/option.model';

@Injectable({
  providedIn: 'root',
})
export class ThreeService implements OnDestroy {
 
  private sharedService = inject(SharedService);
  private destroyRef = inject(DestroyRef);
  private backendApi = inject(BackendCommunicationService);

  private container!: ElementRef<HTMLDivElement>;

  private scene!: Scene;
  private camera!: PerspectiveCamera;
  private renderer!: WebGLRenderer;
  private animationFrameId: number = 0;
  private controls!: OrbitControls;
  private ambientLigth!: AmbientLight;
  private directionalLight!: DirectionalLight;

  private textureUriPath!: string;

  private background = [
    "/images/environment/mockwall2.png",
    "/images/environment/mockwall2.png",
    "/images/environment/mockwall2.png",
    "/images/environment/mockwall2.png",
    "/images/environment/mockwall2.png",
    "/images/environment/mockwall2.png"
  ];

  initialize(container: ElementRef<HTMLDivElement>): void {
    this.container = container;

    if (!this.isWebGl2Supported(this.container)) {
      return;
    }

    this.setScene();
    this.setRenderer();
    this.setCamera();
    this.setLight();
    this.setControls();
    this.setGltfLoader();
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.animationFrameId);
    if (this.renderer) {
      this.renderer.dispose();
    }
  }

  /**
   * In order to adjust the rendered image based on the window size,
   * the method should be called inside the container component when window is resized.
   */
  public resize(): void {
    const containerWidth = this.container.nativeElement.clientWidth;
    const containerHeight = this.container.nativeElement.clientHeight;

    this.camera.aspect = containerWidth / containerHeight;
    this.camera.updateProjectionMatrix();

    this.renderer.setSize(containerWidth, containerHeight);
    this.renderer.setPixelRatio(window.devicePixelRatio);
  }

  private setScene(): void {
    const backgroundEnvironment = new CubeTextureLoader().load(this.background);
    
    this.scene = new Scene();
    this.scene.background = backgroundEnvironment;
  }

  private setRenderer(): void {
    const parameters: WebGLRendererParameters = {antialias: true};
    this.renderer = new WebGLRenderer(parameters);
    this.renderer.setSize(
      this.container.nativeElement.clientWidth,
      this.container.nativeElement.clientHeight
    );
    this.container.nativeElement.appendChild(this.renderer.domElement);
  }

  private setCamera(): void {
    this.camera = new PerspectiveCamera(
      70,
      this.container.nativeElement.clientWidth / this.container.nativeElement.clientHeight,
      1,
      1000
    );
    this.camera.position.set(10, 1.3, -5);
  }

  private setLight(): void {
    this.directionalLight = new DirectionalLight(0xffffff, 1);
    this.directionalLight.position.set(500, 500, 500).normalize();
    this.directionalLight.castShadow = true;
    this.scene.add(this.directionalLight);

    this.ambientLigth = new AmbientLight(0xffffff, 0.5);
    this.scene.add(this.ambientLigth);
  }
    

  private setControls(): void {
    this.controls = new OrbitControls(this.camera, this.renderer.domElement);
    this.controls.enablePan = false;
  }

  private setGltfLoader(): void {
    const loader = new GLTFLoader();
    loader.load(
      '/model/cube.glb',
      (gltf) => {
        const model = gltf.scene;
        this.loadTextures(model);
        this.scene.add(model);
        this.scene.position.y = this.scene.position.y - 1;
        this.animate(gltf);
      },
      undefined,
      (error) => {
        console.error('Oops! We have an error:', error);
      }
    );
  }

  private loadTextures(model: Group<Object3DEventMap>) {
    this.sharedService.selectedCategoryOption$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((selectedItem) => {
        const option: Option = selectedItem.option;
        const category: string = option.category;
        //TODO: 
        console.log("The hell, de ce se cheama de 2 ori: " + option.id);
        
        model.traverse((child) => {
          if ((child as Mesh).isMesh) {
            const mesh: Mesh = child as Mesh;
            let texture;
            
            if (option.texturePath) {
              this.textureUriPath = this.backendApi.getTextureUri(option.texturePath);

              if (mesh.name.includes(category) && option.category === category) {
                  //multiple body because body includes body + hands objects
                  console.log("body texture set: " + this.textureUriPath);
                  texture = new TextureLoader().load(this.textureUriPath);
              }
      
              if (Array.isArray(mesh)) {
                return;
              } else {
                const material = mesh.material as MeshStandardMaterial;
                if (material.map) {
                  if (texture) {
                    texture.flipY = false; // Prevents the rotation of texture. This keeps the same behaviour as in blender
                    material.map = texture;
                    material.needsUpdate = true;
                  }
                }
              }
            }
          }
        });
    });    
  }

  private animate(cube: GLTF) {
    requestAnimationFrame(() => this.animate(cube));
    this.renderer.render(this.scene, this.camera);
    // console.log("x: " + this.camera.position.x + ", y: " + this.camera.position.y + ", z: " + this.camera.position.z);
  }

  private isWebGl2Supported(container: ElementRef<HTMLDivElement>): boolean {
    if (WebGL.isWebGL2Available()) {
      return true;
    }
    const warning = WebGL.getWebGL2ErrorMessage();
    container.nativeElement.appendChild( warning );
    return false;
  }

}