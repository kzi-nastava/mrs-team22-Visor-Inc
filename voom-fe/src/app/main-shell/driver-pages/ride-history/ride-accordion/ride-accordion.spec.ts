import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideAccordion } from './ride-accordion';

describe('RideAccordion', () => {
  let component: RideAccordion;
  let fixture: ComponentFixture<RideAccordion>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideAccordion]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideAccordion);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
