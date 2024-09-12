import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  private selectedCategoryOption = new BehaviorSubject<{optionId: number, navigationId: number}>({optionId: 0, navigationId: 0});

  selectedCategoryOption$ = this.selectedCategoryOption.asObservable();

  setSelectedCategoryOption(optionId: number, navigationId: number) {
    this.selectedCategoryOption.next(
      {optionId: optionId, navigationId: navigationId}
    );
  }

  resetSelectedCategoryOption() {
    this.selectedCategoryOption.next({optionId: 0, navigationId: 0});
  }


}
