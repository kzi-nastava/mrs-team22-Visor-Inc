import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FinishRideDialog } from './finish-ride-dialog';

describe('FinishRideDialog', () => {
  let component: FinishRideDialog;
  let fixture: ComponentFixture<FinishRideDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FinishRideDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FinishRideDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
