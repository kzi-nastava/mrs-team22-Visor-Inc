import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainShell } from './main-shell';

describe('MainShell', () => {
  let component: MainShell;
  let fixture: ComponentFixture<MainShell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainShell]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainShell);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
