import { Component, DestroyRef, inject, input, OnInit, signal } from '@angular/core';
import { OptionButtonComponent } from "../option-button/option-button.component";
import { Option } from '../../model/option.model';
import { BackendCommunicationService } from '../../api/service/backend-communication/backend-communication.service';

@Component({
  selector: 'app-options-navigation',
  standalone: true,
  imports: [OptionButtonComponent],
  templateUrl: './options-navigation.component.html',
  styleUrl: './options-navigation.component.css'
})
export class OptionsNavigationComponent implements OnInit {
  
  public optionNavigationLevel = input.required< 1 | 2 | 3 >();

  protected options = signal<Option[]>([]);

  private backendService = inject(BackendCommunicationService);
  private destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    if (this.optionNavigationLevel() == 1) {
      const subscription = this.backendService.getRootElements()
        .subscribe({
          next: (options) => {
            options.forEach(option => {
              this.options.update(values => [...values, option])
            });
          }
        });
      
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      });
      
    }

  }

 
  // loadChildren(parentId: number) {
  //   this.options.set([]);
  //   const subscription = this.backendService.getChildrenOf(parentId)
  //      .subscribe({
  //        next: (options) => {
  //          options.forEach(option => {
  //            this.options.update(values => [...values, option])
  //          });
  //        }
  //      });
      
  //    this.destroyRef.onDestroy(() => {
  //      subscription.unsubscribe();
  //    });
  // }

  //Asta e chemata de multe ori
  // ngOnChanges(changes: SimpleChanges): void {
  //   console.log("parent path is here: " + this.parentPath());
  // }

}
