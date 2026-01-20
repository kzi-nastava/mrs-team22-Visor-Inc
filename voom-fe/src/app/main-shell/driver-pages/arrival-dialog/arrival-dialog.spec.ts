import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArrivalDialog } from './arrival-dialog';

describe('ArrivalDialog', () => {
  let component: ArrivalDialog;
  let fixture: ComponentFixture<ArrivalDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArrivalDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ArrivalDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
