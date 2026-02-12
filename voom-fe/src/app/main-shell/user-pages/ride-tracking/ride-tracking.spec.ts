import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RideTracking } from './ride-tracking';
import { fakeAsync, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';
import ApiService from '../../../shared/rest/api-service';
import { DriverSimulationWsService } from '../../../shared/websocket/DriverSimulationWsService';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../../../shared/service/authentication-service';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('RideTracking Rating Form', () => {
  let component: RideTracking;
  let fixture: ComponentFixture<RideTracking>;
  let mockApi: any;
  let mockWs: any;

  beforeEach(async () => {
    mockApi = {
      rideApi: {
        getOngoingRide: jasmine.createSpy().and.returnValue(of({ data: null })),
        rateRide: jasmine.createSpy().and.returnValue(of({}))
      }
    };

    mockWs = {
      connect: jasmine.createSpy('connect')
    };

    const mockAuth = {
      activeUser$: of({ id: 1, firstName: 'Vordje', lastName: 'Djujanovic' })
    };

    await TestBed.configureTestingModule({
      imports: [RideTracking, FormsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ApiService, useValue: mockApi },
        { provide: DriverSimulationWsService, useValue: mockWs },
        { provide: AuthenticationService, useValue: mockAuth },
        { provide: ActivatedRoute, useValue: {} },
        { provide: Router, useValue: { navigate: jasmine.createSpy('navigate') } }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(RideTracking);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should show the rating form when rideFinished signal is true', () => {
    let form = fixture.debugElement.query(By.css('.ride-over-title'));
    expect(form).toBeNull();

    component.rideFinished.set(true);
    fixture.detectChanges();

    const title = fixture.debugElement.query(By.css('.ride-over-title')).nativeElement;
    expect(title.textContent).toContain('Your ride is complete!');
  });

  it('should update driverRating signal when a driver star is clicked', () => {
    component.rideFinished.set(true);
    fixture.detectChanges();

    const driverStars = fixture.debugElement.queryAll(By.css('.driver-star'));

    driverStars[3].triggerEventHandler('click', null);

    expect(component.driverRating()).toBe(4);
  });

  it('should update carRating signal when a car star is clicked', () => {
    component.rideFinished.set(true);
    fixture.detectChanges();

    const carStars = fixture.debugElement.queryAll(By.css('.car-star'));

    carStars[4].triggerEventHandler('click', null);

    expect(component.carRating()).toBe(5);
  });

  it('should bind the textarea to reviewComment signal', fakeAsync(() => {
    component.rideFinished.set(true);
    fixture.detectChanges();

    const textarea = fixture.debugElement.query(By.css('textarea')).nativeElement;
    textarea.value = 'The driver was excellent!';
    textarea.dispatchEvent(new Event('input'));

    tick();
    expect(component.reviewComment()).toBe('The driver was excellent!');
  }));

  it('should call rateRide API and show success message on submit', () => {
    component.rideId.set(123);
    component.rideFinished.set(true);
    component.driverRating.set(4);
    component.carRating.set(5);
    component.reviewComment.set('Clean car!');
    fixture.detectChanges();

    const submitBtn = fixture.debugElement.query(By.css('button.bg-blue-900'));
    submitBtn.nativeElement.click();

    expect(mockApi.rideApi.rateRide).toHaveBeenCalledWith(123, {
      driverRating: 4,
      vehicleRating: 5,
      comment: 'Clean car!'
    });

    fixture.detectChanges();
    const thankYouMsg = fixture.debugElement.nativeElement.textContent;
    expect(thankYouMsg).toContain('Thank you for your review');
  });

  it('should show validation error if only driver is rated', () => {
    component.rideFinished.set(true);
    component.driverRating.set(4);
    component.carRating.set(0);
    fixture.detectChanges();

    const errorMsg = fixture.debugElement.nativeElement.textContent;
    expect(errorMsg).toContain('Please provide ratings for both driver and vehicle');

    const submitBtn = fixture.debugElement.query(By.css('button.bg-blue-900'));
    submitBtn.nativeElement.click();
    expect(mockApi.rideApi.rateRide).not.toHaveBeenCalled();
  });

  it('should show validation error if only car is rated', () => {
    component.rideFinished.set(true);
    component.driverRating.set(0);
    component.carRating.set(5);
    fixture.detectChanges();

    const errorMsg = fixture.debugElement.nativeElement.textContent;
    expect(errorMsg).toContain('Please provide ratings for both driver and vehicle');

    expect(mockApi.rideApi.rateRide).not.toHaveBeenCalled();
  });

  it('should show validation error if neither is rated', () => {
    component.rideFinished.set(true);
    component.driverRating.set(0);
    component.carRating.set(0);
    fixture.detectChanges();

    const errorMsg = fixture.debugElement.nativeElement.textContent;
    expect(errorMsg).toContain('Please provide ratings for both driver and vehicle');
  });

  it('should allow submission with minimum valid ratings (1 star) and no comment', () => {
    component.rideId.set(123);
    component.rideFinished.set(true);
    component.driverRating.set(1);
    component.carRating.set(1);
    component.reviewComment.set(''); // em[pty comment is allowed on purpose
    fixture.detectChanges();

    const submitBtn = fixture.debugElement.query(By.css('button.bg-blue-900'));
    submitBtn.nativeElement.click();

    expect(mockApi.rideApi.rateRide).toHaveBeenCalledWith(123, {
      driverRating: 1,
      vehicleRating: 1,
      comment: ''
    });
  });

  it('should highlight exactly the number of stars clicked for driver', () => {
    component.rideFinished.set(true);
    component.setDriverRating(3);
    fixture.detectChanges();

    const stars = fixture.debugElement.queryAll(By.css('.driver-star'));

    expect(stars[0].nativeElement.classList).toContain('text-blue-600');
    expect(stars[1].nativeElement.classList).toContain('text-blue-600');
    expect(stars[2].nativeElement.classList).toContain('text-blue-600');
    expect(stars[3].nativeElement.classList).toContain('text-blue-200');
    expect(stars[4].nativeElement.classList).toContain('text-blue-200');
  });

  it('should hide the form and show success message after successful submission', () => {
    component.rideId.set(123);
    component.rideFinished.set(true);
    component.driverRating.set(5);
    component.carRating.set(5);
    fixture.detectChanges();

    component.submitReview();
    fixture.detectChanges();

    const stars = fixture.debugElement.query(By.css('.driver-star'));
    expect(stars).toBeNull();

    const thankYouMsg = fixture.debugElement.nativeElement.textContent;
    expect(thankYouMsg).toContain('Thank you for your review');
  });

  it('should not call rateRide if rideId is missing', () => {
    component.rideId.set(null);
    component.rideFinished.set(true);
    component.driverRating.set(5);
    component.carRating.set(5);

    component.submitReview();

    expect(mockApi.rideApi.rateRide).not.toHaveBeenCalled();
  });

  it('should submit correctly with a very long comment and valid ratings', () => {
    const longComment = 'A'.repeat(500);
    component.rideId.set(123);
    component.rideFinished.set(true);
    component.driverRating.set(5);
    component.carRating.set(5);
    component.reviewComment.set(longComment);

    component.submitReview();

    expect(mockApi.rideApi.rateRide).toHaveBeenCalledWith(123, jasmine.objectContaining({
      comment: longComment
    }));
  });


});
