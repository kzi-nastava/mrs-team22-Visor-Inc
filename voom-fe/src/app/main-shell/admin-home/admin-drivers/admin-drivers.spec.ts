import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDrivers } from './admin-drivers';

describe('AdminDrivers', () => {
  let component: AdminDrivers;
  let fixture: ComponentFixture<AdminDrivers>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDrivers]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDrivers);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
