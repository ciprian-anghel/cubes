import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { PrintComponent } from "./print/print.component";
import { OptionsNavigationComponent } from "./customization/options-navigation/options-navigation.component";
import { CharacterComponent } from "./customization/character/character.component";
import { SharedService } from './service/shared.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, PrintComponent, OptionsNavigationComponent, CharacterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'cubes';

  private sharedService = inject(SharedService);
  protected levelOneSelection = this.sharedService.levelOneSelection.asReadonly();
  protected levelTwoSelection = this.sharedService.levelTwoSelection.asReadonly();
}
