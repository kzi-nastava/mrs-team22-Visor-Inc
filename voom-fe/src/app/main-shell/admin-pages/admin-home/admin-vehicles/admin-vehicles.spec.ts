import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminVehicles } from './admin-vehicles';

describe('AdminVehicles', () => {
  let component: AdminVehicles;
  let fixture: ComponentFixture<AdminVehicles>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminVehicles]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminVehicles);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
