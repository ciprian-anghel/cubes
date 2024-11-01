import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { OptionsNavigationComponent } from "./customization/options-navigation/options-navigation.component";
import { CharacterCanvasComponent } from "./customization/character/character-canvas.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, OptionsNavigationComponent, CharacterCanvasComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'cubes';

  constructor() {
    localStorage.clear();
  }
  
}
