import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Option } from '../model/option.model';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  private defaultOption: Option = {id: 0, path: "", parentPath: "", iconPath: "", texturePath: "", category:"", name: ""};
  private selectedOption = new BehaviorSubject<{option: Option, navigationId: number}>(
    {option: this.defaultOption, navigationId: 0}
  );

  selectedCategoryOption$ = this.selectedOption.asObservable();

  setSelectedCategoryOption(option: Option, navigationId: number) {
    this.selectedOption.next(
      {option: option, navigationId: navigationId}
    );
  }

  resetSelectedCategoryOption() {
    this.selectedOption.next({option: this.defaultOption, navigationId: 0});
  }

}
