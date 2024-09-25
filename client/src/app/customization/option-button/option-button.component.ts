import { Component, effect, inject, input } from '@angular/core';
import { Option } from '../../model/option.model';
import { SharedService } from '../../service/shared.service';
import { BackendCommunicationService } from '../../api/service/backend-communication/backend-communication.service';
import { DEFAULT_OPTION } from '../../shared/option-default';

@Component({
  selector: 'app-option-button',
  standalone: true,
  imports: [],
  templateUrl: './option-button.component.html',
  styleUrl: './option-button.component.css'
})
export class OptionButtonComponent {
  
  public option = input<Option>(DEFAULT_OPTION);  
  public navigationId = input<number>(0);
  private backendApi = inject(BackendCommunicationService);
  protected imageUrl: string = '';  
  private sharedService = inject(SharedService);

  protected imageLoadError: boolean = false;

  constructor() {
    //TODO: change effect() with something else, not recommended to use effect()
    effect(() => {
      this.imageUrl = this.backendApi.getAssetUri(this.option().iconPath);
    });
  }

  selectOption() {
    this.sharedService.resetSelectedCategoryOption();
    this.sharedService.setSelectedCategoryOption(this.option(), this.navigationId());    
  }

  onImageError() {
    this.imageLoadError = true;
  }

}
