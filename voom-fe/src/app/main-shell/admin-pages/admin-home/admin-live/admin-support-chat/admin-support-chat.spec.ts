import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminSupportChat } from './admin-support-chat';

describe('AdminSupportChat', () => {
  let component: AdminSupportChat;
  let fixture: ComponentFixture<AdminSupportChat>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminSupportChat]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminSupportChat);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
