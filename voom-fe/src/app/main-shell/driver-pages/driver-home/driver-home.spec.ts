import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverHome } from './driver-home';

describe('DriverHome', () => {
  let component: DriverHome;
  let fixture: ComponentFixture<DriverHome>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverHome]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverHome);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
