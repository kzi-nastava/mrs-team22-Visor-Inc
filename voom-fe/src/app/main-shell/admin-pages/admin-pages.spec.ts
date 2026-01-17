import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPages } from './admin-pages';

describe('AdminPages', () => {
  let component: AdminPages;
  let fixture: ComponentFixture<AdminPages>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminPages]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminPages);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
