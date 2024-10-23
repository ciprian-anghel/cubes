import { Component, DestroyRef, inject, input, OnInit, signal } from '@angular/core';
import { OptionButtonComponent } from "../option-button/option-button.component";
import { Option } from '../../model/option.model';
import { BackendCommunicationService } from '../../api/service/backend-communication/backend-communication.service';
import { SharedService } from '../../service/shared.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-options-navigation',
  standalone: true,
  imports: [OptionButtonComponent],
  templateUrl: './options-navigation.component.html',
  styleUrl: './options-navigation.component.css'
})
export class OptionsNavigationComponent implements OnInit {  
  
  public navigationId = input.required< 1 | 2 | 3 >();  
  protected options = signal<Option[]>([]);  

  private sharedService = inject(SharedService);
  private backendService = inject(BackendCommunicationService);

  private subscriptions: Subscription[] = [];
  private destroyRef = inject(DestroyRef);
    
  ngOnInit(): void {
    this.subscriptions.push(this.sharedService.selectedOption$.subscribe((result) => {
      this.loadChildren(result.option.id, result.navigationId);
    }));

    if (this.isInsideNavigationOne()) {
      this.loadRootElements();
    }

    this.destroyRef.onDestroy(() => {
      this.subscriptions.forEach(s => s.unsubscribe());
    });
  }
 
  loadChildren(parentId: number, optionNavigationId: number) {
    if (parentId <= 0) {
      return;
    }

    if (this.isInsideNavigationTwoWithSelectedOptionOnNavigationOne(optionNavigationId)) {
      this.loadOptionsForId(parentId);   

    } else if (this.isInsideNavigationThreeWithSelectedOptionOnNavigationTwo(optionNavigationId)) {
      this.loadOptionsForId(parentId);

    } else if (this.isInsideNavigationThreeWithSelectedOptionOnNavigationOne(optionNavigationId)) {
      this.options.set([]);
    }
  }

  //TODO: Implement error handling
  private loadRootElements() {
    this.subscriptions.push(this.backendService.getRootElements()
        .subscribe({
          next: (options) => {
            options.forEach(option => {
              this.options.update(values => [...values, option])
            });
          }
        }));
  }

  //TODO: Implement error handling
  private loadOptionsForId(parentId: number) {
    this.subscriptions.push(this.backendService.getChildrenOf(parentId)
      .subscribe({
        next: (resultOptions) => {
        this.options.set([]);
        resultOptions.forEach(option => {
            this.options.update(values => [...values, option])
          });
        }
      }));
  }

  private isInsideNavigationOne(): boolean {
    return this.navigationId() === 1;
  }

  private isInsideNavigationTwoWithSelectedOptionOnNavigationOne(optionNavigationId: number): boolean {
    return this.navigationId() === 2 && optionNavigationId === 1;
  }

  private isInsideNavigationThreeWithSelectedOptionOnNavigationTwo(optionNavigationId: number): boolean {
    return this.navigationId() === 3 && optionNavigationId === 2;
  }

  private isInsideNavigationThreeWithSelectedOptionOnNavigationOne(optionNavigationId: number): boolean {
    return this.navigationId() === 3 && optionNavigationId === 1;
  }

}
