import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPricingDialog } from './admin-pricing-dialog';

describe('AdminPricingDialog', () => {
  let component: AdminPricingDialog;
  let fixture: ComponentFixture<AdminPricingDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminPricingDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminPricingDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
