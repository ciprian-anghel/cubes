import { Injectable, ElementRef, OnDestroy } from '@angular/core';
import { Scene, PerspectiveCamera, WebGLRenderer, AmbientLight, DirectionalLight, CubeTextureLoader, WebGLRendererParameters } from 'three';
import { GLTF, GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import WebGL from 'three/examples/jsm/capabilities/WebGL.js';

@Injectable({
  providedIn: 'root',
})
export class ThreeService implements OnDestroy {

  private container!: ElementRef<HTMLDivElement>;

  private scene!: Scene;
  private camera!: PerspectiveCamera;
  private renderer!: WebGLRenderer;
  private animationFrameId: number = 0;
  private controls!: OrbitControls;
  private ambientLigth!: AmbientLight;
  private directionalLight!: DirectionalLight;

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

  public resize(): void {
    const containerWidth = this.container.nativeElement.clientWidth;
    const containerHeight = this.container.nativeElement.clientHeight;

    this.camera.aspect = containerWidth / containerHeight;
    this.camera.updateProjectionMatrix();

    this.renderer.setSize(containerWidth, containerHeight);    
    // Ensure the renderer updates the next frame
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
    this.camera.position.set(10, 1, 0);
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
        this.scene.add(gltf.scene);
        this.scene.position.y = this.scene.position.y - 1;
        this.animate(gltf);        
      },
      undefined,
      (error) => {
        console.error('Oops! We have an error:', error);
      }
    );
  }

  private animate(cube: GLTF) {
    requestAnimationFrame(() => this.animate(cube));
    this.renderer.render(this.scene, this.camera);
    // console.log("x: " + this.camera.position.x + ", y: " + this.camera.position.y + ", z: " + this.camera.position.z);
  }

  ngOnDestroy(): void {
    cancelAnimationFrame(this.animationFrameId);
    if (this.renderer) {
      this.renderer.dispose();
    }
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