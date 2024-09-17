import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import { ThreeService } from '../../service/character-threejs.service';

@Component({
  selector: 'app-character-canvas',
  standalone: true,
  imports: [],
  templateUrl: './character-canvas.component.html',
  styleUrl: './character-canvas.component.css'
})
export class CharacterCanvasComponent implements AfterViewInit, OnDestroy {
  @ViewChild('canvasContainer', { static: true }) canvasContainer!: ElementRef<HTMLDivElement>;

  constructor(private threeService: ThreeService) {}

  ngAfterViewInit(): void {
    this.threeService.initialize(this.canvasContainer);
  }

  ngOnDestroy(): void {
    this.threeService.ngOnDestroy();
  }
}


