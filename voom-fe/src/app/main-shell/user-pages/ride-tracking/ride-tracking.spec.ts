import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideTracking } from './ride-tracking';

// describe('RideTracking', () => {
//   let component: RideTracking;
//   let fixture: ComponentFixture<RideTracking>;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [RideTracking]
//     })
//     .compileComponents();

//     fixture = TestBed.createComponent(RideTracking);
//     component = fixture.componentInstance;
//     fixture.detectChanges();
//   });

//   it('should create', () => {
//     expect(component).toBeTruthy();
//   });
// });

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

fdescribe('RideTracking Rating Form', () => {
  let component: RideTracking;
  let fixture: ComponentFixture<RideTracking>;
  let mockApi: any;
  let mockWs: any;

  beforeEach(async () => {
    // 1. Setup Mocks
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
      activeUser$: of({ id: 1, firstName: 'John' })
    };

    await TestBed.configureTestingModule({
  imports: [RideTracking, FormsModule],
  providers: [
    provideHttpClient(),         // <--- ADD THIS
    provideHttpClientTesting(),  // <--- ADD THIS (best practice for tests)
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

  fit('should show the rating form when rideFinished signal is true', () => {
    // Initially form should not be there
    let form = fixture.debugElement.query(By.css('h2'));
    expect(form).toBeNull();

    // Trigger signal
    component.rideFinished.set(true);
    fixture.detectChanges();

    const title = fixture.debugElement.query(By.css('h2')).nativeElement;
    expect(title.textContent).toContain('Your ride is complete!');
  });

  fit('should update driverRating signal when a driver star is clicked', () => {
    component.rideFinished.set(true);
    fixture.detectChanges();

    const driverStars = fixture.debugElement.queryAll(By.css('.driver-star'));
    
    driverStars[3].triggerEventHandler('click', null);
    
    expect(component.driverRating()).toBe(4);
  });

  fit('should update carRating signal when a car star is clicked', () => {
    component.rideFinished.set(true);
    fixture.detectChanges();

    // Find all stars in the "Car" section (the second flex container of stars)
    const carStars = fixture.debugElement.queryAll(By.css('.car-star'));
    
    carStars[4].triggerEventHandler('click', null);
    
    expect(component.carRating()).toBe(5);
  });

  fit('should bind the textarea to reviewComment signal', fakeAsync(() => {
    component.rideFinished.set(true);
    fixture.detectChanges();

    const textarea = fixture.debugElement.query(By.css('textarea')).nativeElement;
    textarea.value = 'The driver was excellent!';
    textarea.dispatchEvent(new Event('input')); // Required for ngModel/signal sync
    
    tick(); // Wait for signal update
    expect(component.reviewComment()).toBe('The driver was excellent!');
  }));

  fit('should call rateRide API and show success message on submit', () => {
    // Setup state
    component.rideId.set(123);
    component.rideFinished.set(true);
    component.driverRating.set(4);
    component.carRating.set(5);
    component.reviewComment.set('Clean car!');
    fixture.detectChanges();

    // Click submit
    const submitBtn = fixture.debugElement.query(By.css('button.bg-blue-900'));
    submitBtn.nativeElement.click();

    // Assert API call
    expect(mockApi.rideApi.rateRide).toHaveBeenCalledWith(123, {
      driverRating: 4,
      vehicleRating: 5,
      comment: 'Clean car!'
    });

    // Assert UI switches to "Thank you" state
    fixture.detectChanges();
    const thankYouMsg = fixture.debugElement.nativeElement.textContent;
    expect(thankYouMsg).toContain('Thank you for your review');
  });
});
