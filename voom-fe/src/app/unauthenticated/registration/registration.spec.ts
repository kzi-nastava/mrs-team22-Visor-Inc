import {ComponentFixture, TestBed} from '@angular/core/testing';

import {Registration} from './registration';
import ApiService from '../../shared/rest/api-service';
import {Router} from '@angular/router';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideNativeDateAdapter} from '@angular/material/core';
import {ROUTE_UNAUTHENTICATED_MAIN} from '../unauthenticated-main';
import {ROUTE_LOGIN} from '../login/login';

describe('Registration', () => {
  let component: Registration;
  let fixture: ComponentFixture<Registration>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockApiService: jasmine.SpyObj<ApiService>;

  //initialization
  beforeEach(async () => {
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockApiService = jasmine.createSpyObj('ApiService', [], {
      authenticationApi: jasmine.createSpyObj('authenticationApi', ['register'])
    });

    await TestBed.configureTestingModule({
      imports: [Registration],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideNativeDateAdapter(),
        { provide: Router, useValue: mockRouter },
        { provide: ApiService, useValue: mockApiService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Registration);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //personal form tests

  describe('personal form validation', () => {
    it('should initialize personal form with empty values', () => {
      expect(component.personalForm.value).toEqual({
        firstName: '',
        lastName: '',
        birthDate: null
      });
      expect(component.personalForm.invalid).toBeTrue();
    });

    it('should validate firstName as required', () => {
      const firstName = component.personalForm.get('firstName');
      expect(firstName?.valid).toBeFalse();
      expect(firstName?.hasError('required')).toBeTrue();
      expect(component.personalForm.invalid).toBeTrue();
    });

    it('should validate firstName minimum length (2 characters)', () => {
      const firstName = component.personalForm.get('firstName');
      firstName?.setValue('A');
      expect(firstName?.hasError('minlength')).toBeTrue();
      expect(component.personalForm.invalid).toBeTrue();

      firstName?.setValue('Ab');
      expect(firstName?.hasError('minlength')).toBeFalse();
    });

    it('should validate firstName maximum length (255 characters)', () => {
      const firstName = component.personalForm.get('firstName');
      const longName = 'A'.repeat(256);
      firstName?.setValue(longName);
      expect(firstName?.hasError('maxlength')).toBeTrue();
      expect(component.personalForm.invalid).toBeTrue();

      firstName?.setValue('A'.repeat(255));
      expect(firstName?.hasError('maxlength')).toBeFalse();
    });


    it('should validate lastName as required', () => {
      const lastName = component.personalForm.get('lastName');
      expect(lastName?.valid).toBeFalse();
      expect(lastName?.hasError('required')).toBeTrue();
      expect(component.personalForm.invalid).toBeTrue();
    });

    it('should validate lastName minimum and maximum length', () => {
      const lastName = component.personalForm.get('lastName');
      lastName?.setValue('P');
      expect(lastName?.hasError('minlength')).toBeTrue();

      lastName?.setValue('Peterson');
      expect(lastName?.valid).toBeTrue();
    });

    it('should validate birthDate as required', () => {
      const birthDate = component.personalForm.get('birthDate');
      expect(birthDate?.valid).toBeFalse();
      expect(birthDate?.hasError('required')).toBeTrue();
    });

    it('should validate entire personal form with valid data', () => {
      component.personalForm.patchValue({
        firstName: 'Mark',
        lastName: 'Sack',
        birthDate: new Date('2000-05-15')
      });
      expect(component.personalForm.valid).toBeTrue();
    });
  });

  //account form tests

  describe('account form validation', () => {
    it('should initialize account form with empty values', () => {
      expect(component.accountForm.value).toEqual({
        email: '',
        password1: '',
        password2: ''
      });
    });

    it('should validate email as required', () => {
      const email = component.accountForm.get('email');
      expect(email?.hasError('required')).toBeTrue();
      expect(component.accountForm.invalid).toBeTrue();
    });

    it('should validate email format', () => {
      const email = component.accountForm.get('email');

      email?.setValue('invalid-email');
      expect(email?.hasError('email')).toBeTrue();
      expect(component.accountForm.invalid).toBeTrue();

      email?.setValue('valid@email.com');
      expect(email?.hasError('email')).toBeFalse();
      expect(email?.valid).toBeTrue();
    });

    it('should validate email maximum length', () => {
      const email = component.accountForm.get('email');
      const longEmail = 'a'.repeat(250) + '@test.com';
      email?.setValue(longEmail);
      expect(email?.hasError('maxlength')).toBeTrue();
    });

    it('should validate password1 as required', () => {
      const password1 = component.accountForm.get('password1');
      expect(password1?.hasError('required')).toBeTrue();
    });

    it('should validate password1 minimum length (8 characters)', () => {
      const password1 = component.accountForm.get('password1');

      password1?.setValue('short');
      expect(password1?.hasError('minlength')).toBeTrue();

      password1?.setValue('longpass123');
      expect(password1?.hasError('minlength')).toBeFalse();
    });

    it('should validate password2 as required', () => {
      const password2 = component.accountForm.get('password2');
      expect(password2?.hasError('required')).toBeTrue();
    });

    it('should validate entire account form with valid data', () => {
      component.accountForm.patchValue({
        email: 'test@example.com',
        password1: 'password123',
        password2: 'password123'
      });
      expect(component.accountForm.valid).toBeTrue();
    });
  });

  //contact form tests

  describe('contact form validation', () => {
    describe('Contact Form Validation', () => {
      it('should initialize contact form with empty values', () => {
        expect(component.contactForm.value).toEqual({
          address: '',
          phoneNumber: '',
          file: null
        });
      });

      it('should validate address as required', () => {
        const address = component.contactForm.get('address');
        expect(address?.hasError('required')).toBeTrue();
        expect(component.contactForm.invalid).toBeTrue();
      });

      it('should validate address minimum and maximum length', () => {
        const address = component.contactForm.get('address');

        address?.setValue('A');
        expect(address?.hasError('minlength')).toBeTrue();

        address?.setValue('Knez Mihailova 10');
        expect(address?.valid).toBeTrue();

        address?.setValue('A'.repeat(256));
        expect(address?.hasError('maxlength')).toBeTrue();
      });

      it('should validate phoneNumber as required', () => {
        const phoneNumber = component.contactForm.get('phoneNumber');
        expect(phoneNumber?.hasError('required')).toBeTrue();
      });

      it('should validate phoneNumber minimum and maximum length', () => {
        const phoneNumber = component.contactForm.get('phoneNumber');

        phoneNumber?.setValue('1');
        expect(phoneNumber?.hasError('minlength')).toBeTrue();

        phoneNumber?.setValue('+381641234567');
        expect(phoneNumber?.valid).toBeTrue();
      });

      it('should accept file input (optional)', () => {
        const file = component.contactForm.get('file');
        expect(file?.valid).toBeTrue();

        const mockFile = new File(['content'], 'test.pdf', { type: 'application/pdf' });
        file?.setValue(mockFile);
        expect(file?.value).toEqual(mockFile);
      });

      it('should validate entire contact form with valid data', () => {
        component.contactForm.patchValue({
          address: 'Bulevar kralja Aleksandra 73',
          phoneNumber: '+381641234567',
          file: null
        });
        expect(component.contactForm.valid).toBeTrue();
      });
  });

    //register tests


  //redirect to login tests

  describe('redirection to login test', () => {
    it('should navigate to login page when login() is called', () => {
      component.login();

      expect(mockRouter.navigate).toHaveBeenCalledWith([ROUTE_UNAUTHENTICATED_MAIN, ROUTE_LOGIN]);
    });
  });
  });
});
