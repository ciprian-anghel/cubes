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
    this.getCategories().then(categories => {
      let ids: number[] = [];

      categories.forEach(category => {
        let id: number = Number.parseInt(localStorage.getItem(category) || '0');
        if (id > 0) {
          ids.push(id);
        }
      });
     
      this.loading = true;    
      this.backendApi.print(ids)
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
    }).catch(error => {
      console.error("An error occurred while fetching categories:", error);
    });
  }

  private getCategories(): Promise<string[]> {
    return new Promise((resolve, reject) => {
      if (localStorage.getItem('categories')) {
        console.log("reading categories from cache");
        resolve(JSON.parse(localStorage.getItem('categories') || '[]'));    
      } else {
        console.log("reading categories from server");
        this.backendApi.getCategories()
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (categories) => {
            localStorage.setItem('categories', JSON.stringify(categories));
            resolve(JSON.parse(localStorage.getItem('categories') || '[]'));
          },
          error: (error) => {
            console.error("Error retrieving categories from server.", error);
            reject(error);
          }
        });
      }
    }
  )}

}
