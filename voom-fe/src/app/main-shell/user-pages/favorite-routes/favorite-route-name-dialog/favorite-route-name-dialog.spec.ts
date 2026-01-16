import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoriteRouteNameDialog } from './favorite-route-name-dialog';

describe('FavoriteRouteNameDialog', () => {
  let component: FavoriteRouteNameDialog;
  let fixture: ComponentFixture<FavoriteRouteNameDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoriteRouteNameDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoriteRouteNameDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
