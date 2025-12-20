import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderKt1 } from './header-kt1';

describe('HeaderKt1', () => {
  let component: HeaderKt1;
  let fixture: ComponentFixture<HeaderKt1>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderKt1]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderKt1);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
