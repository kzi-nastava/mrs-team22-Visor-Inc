import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValueInputTime } from './value-input-time';

describe('ValueInputTime', () => {
  let component: ValueInputTime;
  let fixture: ComponentFixture<ValueInputTime>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ValueInputTime]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ValueInputTime);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
