import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPanic } from './admin-panic';

describe('AdminPanic', () => {
  let component: AdminPanic;
  let fixture: ComponentFixture<AdminPanic>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminPanic]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminPanic);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
