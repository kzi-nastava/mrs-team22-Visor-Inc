import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminLive } from './admin-live';

describe('AdminLive', () => {
  let component: AdminLive;
  let fixture: ComponentFixture<AdminLive>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminLive]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminLive);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
