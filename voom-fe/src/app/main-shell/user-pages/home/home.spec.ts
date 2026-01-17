import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserHome } from './home';

describe('UserHome', () => {
  let component: UserHome;
  let fixture: ComponentFixture<UserHome>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserHome]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserHome);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
