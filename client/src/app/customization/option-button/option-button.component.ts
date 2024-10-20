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

  getImage() {
    
  }

  selectOption() {
    console.log(this.option().path);
    this.sharedService.resetSelectedCategoryOption();
    this.sharedService.setSelectedCategoryOption(this.option(), this.navigationId());    
  }

  onImageError() {
    this.imageLoadError = true;
  }

}
