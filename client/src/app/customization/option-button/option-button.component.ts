import { Component, effect, inject, input, OnInit } from '@angular/core';
import { Option } from '../../model/option.model';
import { SharedService } from '../../service/shared.service';
import { BackendCommunicationService } from '../../api/service/backend-communication/backend-communication.service';

@Component({
  selector: 'app-option-button',
  standalone: true,
  imports: [],
  templateUrl: './option-button.component.html',
  styleUrl: './option-button.component.css'
})
export class OptionButtonComponent implements OnInit {

  public option = input<Option>({	id: 0, path: '', parentPath: '', iconPath: '', texturePath: '', category: '', name: ''});   //TODO: add a default option
  public navigationId = input<number>(0);

  private backendApi = inject(BackendCommunicationService);

  protected imageUrl = "";
  
  private sharedService = inject(SharedService);

  constructor() {
    //TODO: remove this effect
    effect(() => {
      this.imageUrl = this.backendApi.getTextureUri(this.option().iconPath);
    });
  }

  ngOnInit(): void {
    this.imageUrl = this.backendApi.getTextureUri(this.option().iconPath);
  }

  selectOption() {
    this.sharedService.resetSelectedCategoryOption();

    if (this.option().texturePath) {

    }

    this.sharedService.setSelectedCategoryOption(this.option(), this.navigationId());    
  }

  // private shareSelectedOptions
}
