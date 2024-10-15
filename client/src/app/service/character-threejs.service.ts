import { Injectable, ElementRef, OnDestroy, inject, DestroyRef } from '@angular/core';
import { Scene, PerspectiveCamera, WebGLRenderer, AmbientLight, DirectionalLight, CubeTextureLoader, WebGLRendererParameters, TextureLoader, Mesh, MeshStandardMaterial, Group, Object3DEventMap, SpotLight, GridHelper, AxesHelper} from 'three';
import { GLTF, GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import WebGL from 'three/examples/jsm/capabilities/WebGL.js';
import { SharedService } from './shared.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BackendCommunicationService } from '../api/service/backend-communication/backend-communication.service';
import { Option } from '../model/option.model';
import { environment } from '../../environments/environment';
import { CubeMesh } from '../model/cube-mesh';

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

  private textureUriPath!: string;

  private background = [
    "/images/environment/env-sky-blue.png",
    "/images/environment/env-sky-blue.png",
    "/images/environment/env-sky-blue.png",
    "/images/environment/env-sky-blue.png",
    "/images/environment/env-sky-blue.png",
    "/images/environment/env-sky-blue.png"
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
    // this.gridHelper();
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
      10000
    );
    this.camera.position.set(-10, 0.15, -8.15);
  }

  private setLight(): void {
    const directionalLight = new DirectionalLight(0xffffff, 1);
    directionalLight.position.set(-10, -10, 0).normalize();
    this.scene.add(directionalLight);

    const ambientLigth = new AmbientLight(0xffffff, 3);
    this.scene.add(ambientLigth);

    const spotLight = new SpotLight(0xffffff, 1);
    spotLight.position.set(10, 70, 0);
    spotLight.angle = Math.PI / 6;
    spotLight.penumbra = 0.1;  // Softness of the edge
    spotLight.intensity = 3000;
    this.scene.add(spotLight);

    // const spotLightHelper = new SpotLightHelper(spotLight);
    // this.scene.add(spotLightHelper);
  }
    

  private setControls(): void {
    this.controls = new OrbitControls(this.camera, this.renderer.domElement);
    this.controls.enablePan = false;

    this.controls.minDistance = 10;
    this.controls.maxDistance = 20;
  }

  private setGltfLoader(): void {
    const loader = new GLTFLoader();

    //load decor model
    loader.load(
      '/model/decor.glb',
      (decor) => {
        const model = decor.scene;
        model.position.y = -20;
        this.scene.add(model);
      },
      undefined,
      (error) => {
        console.error('Oops! We have an error:', error);
      }
    );

    //load cube model
    loader.load(
      '/model/cube.glb',
      (cube) => {
        const model = cube.scene;
        model.rotateY(14.5);
        this.loadTextures(model);
        this.scene.add(model);
        this.scene.position.y = this.scene.position.y - 1;
        this.animate(cube);
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
        this.removeMeshesByCategory(option);
        
        model.traverse((child) => {
          if ((child as Mesh).isMesh) {
            const mesh: Mesh = child as Mesh;
            const material = mesh.material as MeshStandardMaterial;

            if (option.color) {
              material.color.set(option.color);
            }
            
            if (option.texturePath) {
              if (material.name.startsWith(option.modelCategory)) {
                const textureOverlay = new TextureLoader().load(environment.serverInstanceUrl + option.texturePath);
                textureOverlay.flipY = false;
                const overlayMaterial = new MeshStandardMaterial({
                  map: textureOverlay,
                  transparent: true, // Enable transparency for the overlay
                  depthTest: true,  // Respect depth when rotating the model.
                  depthWrite: false // Avoid z-fighting, but respect depth
                });
                const overlayMesh: CubeMesh = mesh.clone();
                overlayMesh.category = option.category;
                overlayMesh.material = overlayMaterial;
                overlayMesh.renderOrder = option.renderOrder;
                this.scene.add(overlayMesh);                
              }
            }
          }
        });
    });    
  }

  private removeMeshesByCategory(option: Option) {
    if (!option.category) {
      return;
    }
    
    if (this.scene) {
      this.scene.traverse((child) => {  
        if ((child as CubeMesh).isMesh) {
          
          const mesh: CubeMesh = child as CubeMesh;
          
          const material = mesh.material as MeshStandardMaterial;
          
          if (mesh.category == option.category && option.texturePath) {
            this.scene.remove(mesh);
          }
        }
      });
    }
  }

  private animate(cube: GLTF) {
    requestAnimationFrame(() => this.animate(cube));
    this.renderer.render(this.scene, this.camera);
    console.log("x: " + this.camera.position.x + ", y: " + this.camera.position.y + ", z: " + this.camera.position.z);
  }

  private isWebGl2Supported(container: ElementRef<HTMLDivElement>): boolean {
    if (WebGL.isWebGL2Available()) {
      return true;
    }
    const warning = WebGL.getWebGL2ErrorMessage();
    container.nativeElement.appendChild(warning);
    return false;
  }

  private gridHelper() {
    const gridHelper = new GridHelper(100, 100);
        this.scene.add(gridHelper);

    const axesHelper = new AxesHelper(10);
    this.scene.add(axesHelper);
  }

}