import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApproveChangeProfile } from './approve-change-profile';

describe('ApproveChangeProfile', () => {
  let component: ApproveChangeProfile;
  let fixture: ComponentFixture<ApproveChangeProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApproveChangeProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ApproveChangeProfile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
