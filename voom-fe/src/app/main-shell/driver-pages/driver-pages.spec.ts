import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverPages } from './driver-pages';

describe('DriverPages', () => {
  let component: DriverPages;
  let fixture: ComponentFixture<DriverPages>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverPages]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverPages);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
