import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValueInputString } from './value-input-string';

describe('ValueInputString', () => {
  let component: ValueInputString;
  let fixture: ComponentFixture<ValueInputString>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ValueInputString]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ValueInputString);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
