import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RateRideForm } from './rate-ride-form';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';
import ApiService from '../rest/api-service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

describe('RateRideForm', () => {
  let component: RateRideForm;
  let fixture: ComponentFixture<RateRideForm>;
  let mockApi: any;
  let mockDialogRef: any;
  let mockSnackBar: any;

  beforeEach(async () => {
    mockApi = {
      rideApi: {
        rateRide: jasmine.createSpy('rateRide').and.returnValue(of({}))
      }
    };

    mockDialogRef = {
      close: jasmine.createSpy('close')
    };

    mockSnackBar = {
      open: jasmine.createSpy('open')
    };

    await TestBed.configureTestingModule({
      imports: [RateRideForm, FormsModule],
      providers: [
        { provide: ApiService, useValue: mockApi },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: MAT_DIALOG_DATA, useValue: { rideId: 123 } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RateRideForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should initialize rideId from dialog data', () => {
    expect(component.rideId()).toBe(123);
  });

  it('should update driverRating signal when a driver star is clicked', () => {
    const driverStars = fixture.debugElement.queryAll(By.css('.driver-star'));
    
    driverStars[3].triggerEventHandler('click', null);
    
    expect(component.driverRating()).toBe(4);
  });

  it('should update carRating signal when a car star is clicked', () => {
    const allStars = fixture.debugElement.queryAll(By.css('.car-star'));
    
    allStars[4].triggerEventHandler('click', null);
    
    expect(component.carRating()).toBe(5);
  });

  it('should bind the textarea to reviewComment signal', fakeAsync(() => {
    const textarea = fixture.debugElement.query(By.css('textarea')).nativeElement;
    textarea.value = 'Great service man';
    textarea.dispatchEvent(new Event('input'));
    
    tick();
    expect(component.reviewComment()).toBe('Great service man');
  }));

  it('should call rateRide API, show snackbar, and close dialog on submit', () => {
    component.driverRating.set(4);
    component.carRating.set(5);
    component.reviewComment.set('hell yeah great car');
    fixture.detectChanges();

    const submitBtn = fixture.debugElement.query(By.css('.btn-submit'));
    submitBtn.nativeElement.click();

    expect(mockApi.rideApi.rateRide).toHaveBeenCalledWith(123, {
      driverRating: 4,
      vehicleRating: 5,
      comment: 'hell yeah great car'
    });
    expect(mockSnackBar.open).toHaveBeenCalledWith('Review submitted successfully!', 'Close', jasmine.any(Object));
    expect(mockDialogRef.close).toHaveBeenCalledWith(true);
  });

  it('should show validation error text and snackbar if only driver is rated', () => {
    component.driverRating.set(4);
    component.carRating.set(0);
    fixture.detectChanges();

    const errorMsg = fixture.debugElement.query(By.css('.text-red-600')).nativeElement;
    expect(errorMsg.textContent).toContain('Please provide ratings for both driver and vehicle');

    component.submitReview();
    
    expect(mockApi.rideApi.rateRide).not.toHaveBeenCalled();
    expect(mockSnackBar.open).toHaveBeenCalledWith(jasmine.stringMatching(/Please provide ratings/), 'Close', jasmine.any(Object));
  });

  it('should show validation error text and snackbar if only car is rated', () => {
    component.driverRating.set(0);
    component.carRating.set(5);
    fixture.detectChanges();

    const errorMsg = fixture.debugElement.query(By.css('.text-red-600')).nativeElement;
    expect(errorMsg.textContent).toContain('Please provide ratings for both driver and vehicle');

    component.submitReview();
    expect(mockApi.rideApi.rateRide).not.toHaveBeenCalled();
  });

  it('should show validation error text if neither is rated', () => {
    component.driverRating.set(0);
    component.carRating.set(0);
    fixture.detectChanges();

    const errorMsg = fixture.debugElement.query(By.css('.text-red-600')).nativeElement;
    expect(errorMsg.textContent).toContain('Please provide ratings for both driver and vehicle');
  });

  it('should allow submission with minimum valid ratings (1 star) and no comment', () => {
    component.driverRating.set(1);
    component.carRating.set(1);
    component.reviewComment.set('');
    
    component.submitReview();

    expect(mockApi.rideApi.rateRide).toHaveBeenCalledWith(123, {
      driverRating: 1,
      vehicleRating: 1,
      comment: ''
    });
  });

  it('should highlight exactly the number of stars clicked for driver', () => {
    component.setDriverRating(3);
    fixture.detectChanges();

    const allStars = fixture.debugElement.queryAll(By.css('span.transition'));
    const driverStars = allStars.slice(0, 5);

    expect(driverStars[0].nativeElement.classList).toContain('text-blue-600');
    expect(driverStars[1].nativeElement.classList).toContain('text-blue-600');
    expect(driverStars[2].nativeElement.classList).toContain('text-blue-600');
    expect(driverStars[3].nativeElement.classList).toContain('text-blue-200');
    expect(driverStars[4].nativeElement.classList).toContain('text-blue-200');
  });

  it('should highlight exactly the number of stars clicked for car', () => {
    component.setCarRating(2);
    fixture.detectChanges();

    const allStars = fixture.debugElement.queryAll(By.css('span.transition'));
    const carStars = allStars.slice(5, 10);

    expect(carStars[0].nativeElement.classList).toContain('text-blue-600');
    expect(carStars[1].nativeElement.classList).toContain('text-blue-600');
    expect(carStars[2].nativeElement.classList).toContain('text-blue-200');
  });

  it('should not call rateRide if rideId is missing', () => {
    component.rideId.set(null);
    component.driverRating.set(5);
    component.carRating.set(5);

    component.submitReview();

    expect(mockApi.rideApi.rateRide).not.toHaveBeenCalled();
  });

  it('should submit correctly with a very long comment and valid ratings', () => {
    const longComment = 'Exemplary service '.repeat(30);
    component.driverRating.set(5);
    component.carRating.set(5);
    component.reviewComment.set(longComment);

    component.submitReview();

    expect(mockApi.rideApi.rateRide).toHaveBeenCalledWith(123, {
      driverRating: 5,
      vehicleRating: 5,
      comment: longComment
    });
  });
});