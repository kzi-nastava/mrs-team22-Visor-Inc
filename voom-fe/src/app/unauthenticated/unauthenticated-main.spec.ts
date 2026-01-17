import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnauthenticatedMain } from './unauthenticated-main';

describe('UnauthenticatedMain', () => {
  let component: UnauthenticatedMain;
  let fixture: ComponentFixture<UnauthenticatedMain>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnauthenticatedMain]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UnauthenticatedMain);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
