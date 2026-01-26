import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanicDialog } from './panic-dialog';

describe('PanicDialog', () => {
  let component: PanicDialog;
  let fixture: ComponentFixture<PanicDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanicDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PanicDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
