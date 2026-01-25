import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminVehiclesDialog } from './admin-vehicles-dialog';

describe('AdminVehiclesDialog', () => {
  let component: AdminVehiclesDialog;
  let fixture: ComponentFixture<AdminVehiclesDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminVehiclesDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminVehiclesDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
