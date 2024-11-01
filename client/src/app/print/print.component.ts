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

  print() {
    const categories = this.getCategories();
    let ids: number[] = [];
    let baseColorId: number = 0;

    categories.forEach(category => {
      let id: number = Number.parseInt(localStorage.getItem(category) || '0');
      if (id > 0) {
        ids.push(id);
      }
    });
    baseColorId = Number.parseInt(localStorage.getItem('color') || '0');
 
    this.loading = true;    
    this.backendApi.print(ids, baseColorId)
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

  private getCategories(): string[] {
    if (localStorage.getItem('categories')) {
      console.log("reading categories from cache");
    } else {
      console.log("reading categories from server");
      this.backendApi.getCategories()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (categories) => {
          localStorage.setItem('categories', JSON.stringify(categories));
        },
        error: (error) => {
          console.error("Error retrieving categories from server.", error);
        }
      });
    }
    return JSON.parse(localStorage.getItem('categories') || '[]');
  }

}
