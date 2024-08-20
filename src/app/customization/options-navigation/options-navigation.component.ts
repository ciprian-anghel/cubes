import { Component } from '@angular/core';
import { OptionButtonComponent } from "../option-button/option-button.component";

@Component({
  selector: 'app-options-navigation',
  standalone: true,
  imports: [OptionButtonComponent],
  templateUrl: './options-navigation.component.html',
  styleUrl: './options-navigation.component.css'
})
export class OptionsNavigationComponent {

}
