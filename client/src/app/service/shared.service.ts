import { Injectable, input, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  sharedData = signal<number>(0);

  constructor() { }

  setData(id: number) {
    console.log("Value set: " + id);
    this.sharedData.set(id);
  }

  getData(): number {
    let val = this.sharedData();
    console.log("Value retrieved: " + val);
    return val;
  }

}
