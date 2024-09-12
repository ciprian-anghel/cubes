import { Component, DestroyRef, inject, input, computed, OnInit, signal, effect } from '@angular/core';
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
  
  public componentId = input.required< 1 | 2 | 3 >();
  public buttonLevel = computed(() => this.componentId());
  public optionSelectedId = input<number>(0);

  protected options = signal<Option[]>([]);  

  private backendService = inject(BackendCommunicationService);
  private destroyRef = inject(DestroyRef);

  //TODO: convert effect to computes --- per documentation, effect should not be used for this scenario
  constructor() {
    effect(() => {
      this.loadChildren(this.optionSelectedId())
    });
  } 

  ngOnInit(): void {
    if (this.componentId() == 1) {
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

 
  loadChildren(parentId: number) {
    if (parentId <= 0) return;

    const subscription = this.backendService.getChildrenOf(parentId)
       .subscribe({
         next: (resultOptions) => {
          this.options.set([]);
          resultOptions.forEach(option => {
             this.options.update(values => [...values, option])
           });
         }
       });
      
     this.destroyRef.onDestroy(() => {
       subscription.unsubscribe();
     });
  }

  //Asta e chemata de multe ori
  // ngOnChanges(changes: SimpleChanges): void {
  //   console.log("parent path is here: " + this.parentPath());
  // }

}
