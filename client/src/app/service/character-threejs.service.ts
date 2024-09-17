import { Injectable, ElementRef, OnDestroy } from '@angular/core';
import { Scene, PerspectiveCamera, WebGLRenderer, AmbientLight, DirectionalLight } from 'three';
import WebGL from 'three/examples/jsm/capabilities/WebGL.js';
import { GLTF, GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';


@Injectable({
  providedIn: 'root',
})
export class ThreeService implements OnDestroy {
  private scene!: Scene;
  private camera!: PerspectiveCamera;
  private renderer!: WebGLRenderer;
  private animationFrameId: number = 0;
  private controls!: OrbitControls;

  private sofLight!: AmbientLight;
  private directionalLight!: DirectionalLight;

  initialize(container: ElementRef<HTMLDivElement>): void {
    if (!this.isWebGl2Supported(container)) {
      return;
    }

    this.setScene();
    this.setRenderer(container);
    this.setCamera(container);
    this.setLight();
    this.setControls();
    this.setGltfLoader();   
  }

  private setScene(): void {
    this.scene = new Scene();
  }

  private setRenderer(container: ElementRef<HTMLDivElement>): void {
    this.renderer = new WebGLRenderer();
    this.renderer.setSize(
      container.nativeElement.clientWidth,
      container.nativeElement.clientHeight
    );
    container.nativeElement.appendChild(this.renderer.domElement);
  }

  private setCamera(container: ElementRef<HTMLDivElement>): void {
    this.camera = new PerspectiveCamera(
      70,
      container.nativeElement.clientWidth / container.nativeElement.clientHeight,
      1,
      1000
    );
    this.camera.position.set(0, 0, 15);
  }

  private setLight(): void {
    this.sofLight = new AmbientLight(0xffffff, 0.5); // Soft white light
    this.directionalLight = new DirectionalLight(0xffffff, 1);
    this.directionalLight.position.set(0, 1, 1).normalize(); // Direction of the light
  }

  private setControls(): void {
    this.controls = new OrbitControls(this.camera, this.renderer.domElement);
    this.controls.enablePan = false;
  }

  private setGltfLoader(): void {
    const loader = new GLTFLoader();
    loader.load(
      '/cube.glb',
      (gltf) => {
        this.scene.add(gltf.scene);        
        this.scene.add(this.sofLight);
        this.scene.add(this.directionalLight);

        this.animate(gltf);        
      },
      undefined,
      (error) => {
        console.error('An error happened:', error);
      }
    );
  }

  private animate(cube: GLTF) {
    requestAnimationFrame(() => this.animate(cube));
    this.renderer.render(this.scene, this.camera);
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
