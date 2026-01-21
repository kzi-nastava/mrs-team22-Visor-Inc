import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDriversDialog } from './admin-drivers-dialog';

describe('AdminDriversDialog', () => {
  let component: AdminDriversDialog;
  let fixture: ComponentFixture<AdminDriversDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDriversDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDriversDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
