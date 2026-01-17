import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifyProfile } from './verify-profile';

describe('VerifyProfile', () => {
  let component: VerifyProfile;
  let fixture: ComponentFixture<VerifyProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifyProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerifyProfile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
