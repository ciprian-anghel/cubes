import { Component, effect, inject, input, OnInit } from '@angular/core';
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
export class OptionButtonComponent implements OnInit {

  public option = input<Option>({	id: 0, path: '', parentPath: '', iconPath: '', texturePath: '', name: ''});   //TODO: add a default option
  public navigationId = input<number>(0);

  protected imageUrl = "";
  
  private sharedService = inject(SharedService);
  private readonly serverInstanceUrl: string = environment.serverInstanceUrl;

  constructor() {
    //TODO: remove this effect
    effect(() => {
      this.imageUrl = this.serverInstanceUrl + this.option().iconPath;
    });
  }

  ngOnInit(): void {
    this.imageUrl = this.serverInstanceUrl + this.option().iconPath;
  }

  selectOption() {
    this.sharedService.reset();
    if (!this.option().texturePath) {
      this.sharedService.setSelectedOptionState(this.option().id, this.navigationId());
    }
  }
}
