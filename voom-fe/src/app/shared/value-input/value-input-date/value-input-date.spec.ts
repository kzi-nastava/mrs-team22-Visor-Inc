import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValueInputDate } from './value-input-date';

describe('ValueInputDate', () => {
  let component: ValueInputDate;
  let fixture: ComponentFixture<ValueInputDate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ValueInputDate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ValueInputDate);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
