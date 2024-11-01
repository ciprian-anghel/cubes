import { Component, DestroyRef, inject } from '@angular/core';
import { BackendCommunicationService } from '../api/service/backend-communication/backend-communication.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-print',
  standalone: true,
  imports: [],
  templateUrl: './print.component.html',
  styleUrl: './print.component.css'
})
export class PrintComponent {  

  private backendApi = inject(BackendCommunicationService);
  private destroyRef = inject(DestroyRef);
  
  loading: boolean = false;

  onClick() {   
    this.loading = true;    
    this.backendApi.print()
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe({
      next: (response) => {
        const blob = response.body;
        const contentDisposition = response.headers.get('Content-Disposition');
        const filename = contentDisposition 
          ? contentDisposition.split('filename=')[1].replace(/"/g, '')
          : 'downloaded-file.pdf';

          if (blob) {
            saveAs(blob, filename);
          } else {
            console.error("File received was empty.");
          }
      },      
      complete: () => {
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        console.error("Error generating printable file:", error);
      }
    });
  }

}
