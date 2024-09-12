import { Component, effect, inject, input, OnChanges, OnInit, signal, SimpleChanges } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Option } from '../../model/option.model';
import { SharedService } from '../../service/shared.service';

@Component({
  selector: 'app-option-button',
  standalone: true,
  imports: [],
  templateUrl: './option-button.component.html',
  styleUrl: './option-button.component.css'
})
export class OptionButtonComponent implements OnInit, OnChanges {

  public option = input<Option>({	id: 0, path: '', parentPath: '', iconPath: '', texturePath: '', name: ''});   //TODO: add a default option
 
  protected imageUrl = "";
  
  private sharedService = inject(SharedService);
  private readonly serverInstanceUrl: string = environment.serverInstanceUrl;

  constructor() {
    effect(() => {
      console.log("Updates img url");
      this.imageUrl = this.serverInstanceUrl + this.option().iconPath;
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    //TODO: In order to fully use signals, and not call lifecycle hooks
    //      I should remove any input and convert it to signal. And then somehow
    //      pass the values from parent to child. Maybe use the effect
    // console.log(">>>> on changes");
  }

  ngOnInit(): void {
    console.log("Init first img url");
    this.imageUrl = this.serverInstanceUrl + this.option().iconPath;
  }

  selectOption() {
    console.log("Option selected: " + this.option().id);
    if (!this.option().texturePath) {
      
    }    
  }

  

}
