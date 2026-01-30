import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityMap } from './activity-map';

describe('ActivityMap', () => {
  let component: ActivityMap;
  let fixture: ComponentFixture<ActivityMap>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivityMap]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivityMap);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
