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
    this.saveOptionToLocalStorage(option);
  }

  clearTexture(option: Option): boolean {
    if (!option.scheduledToClearTexture) {
      return false;
    }
    
    option.scheduledToClearTexture = false;
    option.selected = false;
    this.removeOptionFromStorage(option);
    return true;
  } 

  private saveOptionToLocalStorage(option: Option) {
    if (option.texturePath) {
      localStorage.setItem(option.category, option.id.toString());
    }
  }

  private removeOptionFromStorage(option: Option) {
    if (option.texturePath) {
      localStorage.removeItem(option.category);
    }
  }

  resetSelectedOption() {
    this.selectedOption.next({option: DEFAULT_OPTION, navigationId: 0});
  }

}
