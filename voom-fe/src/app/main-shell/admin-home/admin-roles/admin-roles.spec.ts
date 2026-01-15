import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminRoles } from './admin-roles';

describe('AdminRoles', () => {
  let component: AdminRoles;
  let fixture: ComponentFixture<AdminRoles>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminRoles]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminRoles);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
