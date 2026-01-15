import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminActivity } from './admin-activity';

describe('AdminActivity', () => {
  let component: AdminActivity;
  let fixture: ComponentFixture<AdminActivity>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminActivity]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminActivity);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
