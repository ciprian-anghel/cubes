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

  selectedOption$ = this.selectedOption.asObservable();

  setSelectedOption(option: Option, navigationId: number) {
    this.selectedOption.next(
      {option: option, navigationId: navigationId}
    );
  }

  resetSelectedOption() {
    this.selectedOption.next({option: DEFAULT_OPTION, navigationId: 0});
  }

}
