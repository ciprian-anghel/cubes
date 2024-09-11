import { Component, inject, input, OnInit, signal } from '@angular/core';
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

  public  option = input<Option>({	id: 0, path: '', parentPath: '', iconPath: '', texturePath: '', name: ''});   //TODO: add a default option

  protected imageUrl = signal<string | undefined>(''); 
  
  private sharedServce = inject(SharedService);
  private readonly serverInstanceUrl: string = environment.serverInstanceUrl;

  ngOnInit(): void {
    this.imageUrl.set(this.serverInstanceUrl + this.option().iconPath);
  }

  selectOption() {
    console.log("Option selected: " + this.option().id);
  }

}
