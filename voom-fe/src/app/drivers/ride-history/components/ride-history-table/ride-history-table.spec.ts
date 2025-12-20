import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideHistoryTable } from './ride-history-table';

describe('RideHistoryTable', () => {
  let component: RideHistoryTable;
  let fixture: ComponentFixture<RideHistoryTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistoryTable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideHistoryTable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
