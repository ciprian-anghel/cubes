import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Option } from '../model/option.model';
import { DEFAULT_OPTION } from '../shared/option-default';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  private selectedOption = new BehaviorSubject<{option: Option, navigationId: number}>(
    {option: DEFAULT_OPTION, navigationId: 0}
  );

  selectedCategoryOption$ = this.selectedOption.asObservable();

  setSelectedCategoryOption(option: Option, navigationId: number) {
    this.selectedOption.next(
      {option: option, navigationId: navigationId}
    );
  }

  resetSelectedCategoryOption() {
    this.selectedOption.next({option: DEFAULT_OPTION, navigationId: 0});
  }

}
