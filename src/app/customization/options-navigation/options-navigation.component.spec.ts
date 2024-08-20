import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OptionsNavigationComponent } from './options-navigation.component';

describe('OptionsNavigationComponent', () => {
  let component: OptionsNavigationComponent;
  let fixture: ComponentFixture<OptionsNavigationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OptionsNavigationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OptionsNavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
