import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RateRideForm } from './rate-ride-form';

describe('RateRideForm', () => {
  let component: RateRideForm;
  let fixture: ComponentFixture<RateRideForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RateRideForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RateRideForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
