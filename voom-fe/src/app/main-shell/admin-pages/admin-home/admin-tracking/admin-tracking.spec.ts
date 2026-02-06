import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTracking } from './admin-tracking';

describe('AdminTracking', () => {
  let component: AdminTracking;
  let fixture: ComponentFixture<AdminTracking>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTracking]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminTracking);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
