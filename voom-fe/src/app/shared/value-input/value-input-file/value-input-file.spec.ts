import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValueInputFile } from './value-input-file';

describe('ValueInputFile', () => {
  let component: ValueInputFile;
  let fixture: ComponentFixture<ValueInputFile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ValueInputFile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ValueInputFile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
