import { Component, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-character',
  standalone: true,
  imports: [],
  templateUrl: './character.component.html',
  styleUrl: './character.component.css'
})
export class CharacterComponent implements OnChanges {

  ngOnChanges(changes: SimpleChanges): void {
    console.log("onChange");
  }

}
