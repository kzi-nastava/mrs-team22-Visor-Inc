import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPricing } from './admin-pricing';

describe('AdminPricing', () => {
  let component: AdminPricing;
  let fixture: ComponentFixture<AdminPricing>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminPricing]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminPricing);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
