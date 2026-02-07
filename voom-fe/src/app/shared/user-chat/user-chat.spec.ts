import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserChat } from './user-chat';

describe('UserChat', () => {
  let component: UserChat;
  let fixture: ComponentFixture<UserChat>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserChat]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserChat);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
