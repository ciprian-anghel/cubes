import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  private selectedOptionState = new BehaviorSubject<{optionId: number, navigationId: number}>({optionId: 0, navigationId: 0});

  navigationData$ = this.selectedOptionState.asObservable();

  setSelectedOptionState(optionId: number, navigationId: number) {
    this.selectedOptionState.next(
      {optionId: optionId, navigationId: navigationId}
    );
  }

  reset() {
    this.selectedOptionState.next({optionId: 0, navigationId: 0});
  }
}
