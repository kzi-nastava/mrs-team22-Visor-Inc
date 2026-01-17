import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoriteRouteAccordion } from './favorite-routes-accordition';

describe('FavoriteRouteAccordion', () => {
  let component: FavoriteRouteAccordion;
  let fixture: ComponentFixture<FavoriteRouteAccordion>;  
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoriteRouteAccordion]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoriteRouteAccordion);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
