import { Component, DestroyRef, effect, inject, input } from '@angular/core';
import { Option } from '../../model/option.model';
import { SharedService } from '../../service/shared.service';
import { BackendCommunicationService } from '../../api/service/backend-communication/backend-communication.service';
import { DEFAULT_OPTION } from '../../shared/option-default';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

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
  protected imageLoaded: boolean = false;

  private destroyRef = inject(DestroyRef);

  constructor() {
    effect(() => {
      this.backendApi.getAsset(this.option().iconPath)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (asset: Blob) => {
          this.imageUrl = URL.createObjectURL(asset);
          this.imageLoadError = false;
        },
        error: (error) => {
          console.error("Error fetching asset:", error);
        }
      });
    });
  }

  /**
   * At each click, this.option().selected is set to false to all buttons then is set to true only to the selected button.
   * see OptionsNavigationComponent
   * 
   * Also, the selection is set back to false when the texture is cleared.
   * see ThreeService
   */
  selectOption() {
    if (this.option().selected) {      
      this.option().toClearTexture = true;      
    }

    localStorage.setItem(this.option().category, this.option().id.toString());
    this.sharedService.setSelectedOption(this.option(), this.navigationId());
  }

  onImageError() {
    this.imageLoadError = true;
  }

  onLoad() {
    this.imageLoaded = true;
  }

}
