import { Injectable, input, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  levelOneSelection = signal<number>(0);
  levelTwoSelection = signal<number>(0);

}
