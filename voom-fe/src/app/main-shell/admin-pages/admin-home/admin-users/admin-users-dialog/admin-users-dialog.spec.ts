import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminUsersDialog } from './admin-users-dialog';

describe('AdminUsersDialog', () => {
  let component: AdminUsersDialog;
  let fixture: ComponentFixture<AdminUsersDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminUsersDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminUsersDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
