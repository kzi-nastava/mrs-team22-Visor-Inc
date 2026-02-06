import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminBlockUserDialog } from './admin-block-user-dialog';

describe('AdminBlockUserDialog', () => {
  let component: AdminBlockUserDialog;
  let fixture: ComponentFixture<AdminBlockUserDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminBlockUserDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminBlockUserDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
