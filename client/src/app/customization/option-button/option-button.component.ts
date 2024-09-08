import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { BackendCommunicationService } from '../../api/service/backend-communication/backend-communication.service';

@Component({
  selector: 'app-option-button',
  standalone: true,
  imports: [],
  templateUrl: './option-button.component.html',
  styleUrl: './option-button.component.css'
})
export class OptionButtonComponent implements OnInit {

  private apiService = inject(BackendCommunicationService);
  private destroyRef = inject(DestroyRef);
  
  imageUrl = signal<string | undefined>('http://localhost:8080/assets/cubes/icon-head.png');

  private tempAssetPath: string = 'localStorage/cubes/body/body-cyan.png';

  ngOnInit(): void {
    // const subscription = this.apiService.downloadAsset(this.tempAssetPath)
    //   .subscribe({
    //     next: (asset) => {          
    //       this.base64Image.set(asset.data);
    //     },
    //   });

    // this.destroyRef.onDestroy(() => {
    //   subscription.unsubscribe();
    // });
  }

}
