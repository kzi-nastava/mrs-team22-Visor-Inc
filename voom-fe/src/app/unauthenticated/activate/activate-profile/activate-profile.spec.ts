import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivateProfile } from './activate-profile';

describe('ActivateProfile', () => {
  let component: ActivateProfile;
  let fixture: ComponentFixture<ActivateProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivateProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivateProfile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
