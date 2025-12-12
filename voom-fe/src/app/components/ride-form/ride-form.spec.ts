import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideForm } from './ride-form';

describe('RideForm', () => {
  let component: RideForm;
  let fixture: ComponentFixture<RideForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
