import { Injectable, input, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  sharedData = signal<number>(0);

}
