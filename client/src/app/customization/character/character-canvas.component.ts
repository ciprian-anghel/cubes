import { AfterViewInit, Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ThreeService } from '../../service/character-threejs.service';
import { fromEvent, Observable, Subscription } from 'rxjs';

@Component({
  selector: 'app-character-canvas',
  standalone: true,
  imports: [],
  templateUrl: './character-canvas.component.html',
  styleUrl: './character-canvas.component.css'
})
export class CharacterCanvasComponent implements AfterViewInit, OnDestroy, OnInit {
  @ViewChild('canvasContainer') canvasContainer!: ElementRef<HTMLDivElement>;

  private resizeObservable$!: Observable<Event>;
  private resizeSubscription$!: Subscription;

  constructor(private threeService: ThreeService) {}

  ngOnInit(): void {
    this.resizeObservable$ = fromEvent(window, 'resize');
    this.resizeSubscription$ = this.resizeObservable$.subscribe( evt => {
      const element = this.canvasContainer.nativeElement;
      const height = element.offsetHeight;
      const width = element.offsetWidth;
      this.threeService.resize(height, width);
    });
  }

  ngAfterViewInit(): void {
    this.threeService.initialize(this.canvasContainer);
  }

  ngOnDestroy(): void {
    this.threeService.ngOnDestroy();
    this.resizeSubscription$.unsubscribe();
  }
  
}


