import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduledRides } from './scheduled-rides';

describe('ScheduledRides', () => {
  let component: ScheduledRides;
  let fixture: ComponentFixture<ScheduledRides>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScheduledRides]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduledRides);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
