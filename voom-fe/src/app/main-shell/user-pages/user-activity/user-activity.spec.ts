import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserActivity } from './user-activity';

describe('UserActivity', () => {
  let component: UserActivity;
  let fixture: ComponentFixture<UserActivity>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserActivity]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserActivity);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
