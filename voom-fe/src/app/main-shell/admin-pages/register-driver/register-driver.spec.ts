import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminRegisterDriver } from './register-driver';

describe('AdminRegisterDriver', () => {
  let component: AdminRegisterDriver;
  let fixture: ComponentFixture<AdminRegisterDriver>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminRegisterDriver]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminRegisterDriver);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
