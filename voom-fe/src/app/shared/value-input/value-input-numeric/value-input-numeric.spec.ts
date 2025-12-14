import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValueInputNumeric } from './value-input-numeric';

describe('ValueInputNumeric', () => {
  let component: ValueInputNumeric;
  let fixture: ComponentFixture<ValueInputNumeric>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ValueInputNumeric]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ValueInputNumeric);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
