import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPages } from './user-pages';

describe('UserPages', () => {
  let component: UserPages;
  let fixture: ComponentFixture<UserPages>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserPages]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserPages);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
