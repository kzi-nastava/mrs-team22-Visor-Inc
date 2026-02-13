import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminUsersDialog } from './admin-users-dialog';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import ApiService from '../../../../../shared/rest/api-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { UserStatus } from '../../../../../shared/rest/user/user.model';
import { provideNativeDateAdapter } from '@angular/material/core';

describe('Driver registration dialog', () => {
  let component: AdminUsersDialog;
  let fixture: ComponentFixture<AdminUsersDialog>;

  let mockDialogRef: jasmine.SpyObj<MatDialogRef<AdminUsersDialog>>;
  let mockApiService: jasmine.SpyObj<ApiService>;
  let mockSnackBar: jasmine.SpyObj<MatSnackBar>;

  beforeEach(async () => {
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);

    mockSnackBar = jasmine.createSpyObj('MatSnackBar', ['open']);

    mockApiService = jasmine.createSpyObj('ApiService', [], {
      userApi: jasmine.createSpyObj('userApi', ['createUser']),
    });

    const mockRoles = [
      { id: '1', roleName: 'DRIVER' },
      { id: '2', roleName: 'USER' },
    ];

    await TestBed.configureTestingModule({
      imports: [AdminUsersDialog],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideNativeDateAdapter(),
        { provide: ApiService, useValue: mockApiService },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: MAT_DIALOG_DATA, useValue: mockRoles },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminUsersDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test form initialization
  describe('form initialization', () => {
    it('should initialize form with empty values', () => {
      expect(component.userForm.value).toEqual({
        userRole: null,
        userStatus: null,
        firstName: '',
        lastName: '',
        birthDate: null,
        email: '',
        address: '',
        phoneNumber: '',
        password: '',
      });

      expect(component.userForm.invalid).toBeTrue();
    });
  });

  describe('required validation', () => {
    it('should require userRole', () => {
      const control = component.userForm.get('userRole');
      expect(control?.hasError('required')).toBeTrue();
    });

    it('should require userStatus', () => {
      const control = component.userForm.get('userStatus');
      expect(control?.hasError('required')).toBeTrue();
    });

    it('should require firstName', () => {
      const control = component.userForm.get('firstName');
      expect(control?.hasError('required')).toBeTrue();
    });

    it('should require lastName', () => {
      const control = component.userForm.get('lastName');
      expect(control?.hasError('required')).toBeTrue();
    });

    it('should require birthDate', () => {
      const control = component.userForm.get('birthDate');
      expect(control?.hasError('required')).toBeTrue();
    });

    it('should require email', () => {
      const control = component.userForm.get('email');
      expect(control?.hasError('required')).toBeTrue();
    });

    it('should require address', () => {
      const control = component.userForm.get('address');
      expect(control?.hasError('required')).toBeTrue();
    });

    it('should require phoneNumber', () => {
      const control = component.userForm.get('phoneNumber');
      expect(control?.hasError('required')).toBeTrue();
    });

    it('should require password', () => {
      const control = component.userForm.get('password');
      expect(control?.hasError('required')).toBeTrue();
    });
  });

  describe('name validation', () => {
    it('should validate firstName minlength', () => {
      const control = component.userForm.get('firstName');

      control?.setValue('A');
      expect(control?.hasError('minlength')).toBeTrue();

      control?.setValue('Marko');
      expect(control?.hasError('minlength')).toBeFalse();
    });

    it('should validate firstName maxlength', () => {
      const control = component.userForm.get('firstName');

      control?.setValue('A'.repeat(256));
      expect(control?.hasError('maxlength')).toBeTrue();
    });

    it('should validate lastName minlength', () => {
      const control = component.userForm.get('lastName');

      control?.setValue('A');
      expect(control?.hasError('minlength')).toBeTrue();

      control?.setValue('Petrovic');
      expect(control?.valid).toBeTrue();
    });

    it('should validate lastName maxlength', () => {
      const control = component.userForm.get('lastName');

      control?.setValue('A'.repeat(256));
      expect(control?.hasError('maxlength')).toBeTrue();
    });
  });

  describe('email validation', () => {
    it('should validate email format', () => {
      const email = component.userForm.get('email');

      email?.setValue('invalid-email');
      expect(email?.hasError('email')).toBeTrue();

      email?.setValue('valid@email.com');
      expect(email?.hasError('email')).toBeFalse();
    });

    it('should validate email maxlength', () => {
      const email = component.userForm.get('email');

      const longEmail = 'a'.repeat(250) + '@test.com';
      email?.setValue(longEmail);

      expect(email?.hasError('maxlength')).toBeTrue();
    });
  });

  describe('password validation', () => {
    it('should validate password minlength', () => {
      const password = component.userForm.get('password');

      password?.setValue('short');
      expect(password?.hasError('minlength')).toBeTrue();

      password?.setValue('longpassword123');
      expect(password?.hasError('minlength')).toBeFalse();
    });
  });

  describe('contact validation', () => {
    it('should validate address minlength and maxlength', () => {
      const address = component.userForm.get('address');

      address?.setValue('A');
      expect(address?.hasError('minlength')).toBeTrue();

      address?.setValue('Bulevar kralja Aleksandra 10');
      expect(address?.valid).toBeTrue();

      address?.setValue('A'.repeat(256));
      expect(address?.hasError('maxlength')).toBeTrue();
    });

    it('should validate phoneNumber minlength', () => {
      const phone = component.userForm.get('phoneNumber');

      phone?.setValue('1');
      expect(phone?.hasError('minlength')).toBeTrue();

      phone?.setValue('+381641234567');
      expect(phone?.valid).toBeTrue();
    });
  });

  describe('form valid state', () => {
    it('should be valid when all fields are correct', () => {
      component.userForm.patchValue({
        userRole: { id: 1, roleName: 'DRIVER' },
        userStatus: UserStatus.ACTIVE,
        firstName: 'Marko',
        lastName: 'Markovic',
        birthDate: new Date('2000-05-15'),
        email: 'marko@example.com',
        address: 'Bulevar 10',
        phoneNumber: '+381641234567',
        password: 'password123',
      });

      expect(component.userForm.valid).toBeTrue();
    });
  });

  describe('submit tests', () => {
    beforeEach(() => {
      component.userForm.patchValue({
        userRole: { id: 1, roleName: 'DRIVER' },
        userStatus: UserStatus.ACTIVE,
        firstName: 'Marko',
        lastName: 'Markovic',
        birthDate: new Date('2000-05-15'),
        email: 'marko@example.com',
        address: 'Bulevar 10',
        phoneNumber: '+381641234567',
        password: 'password123',
      });
    });

    it('should call createUser with correct DTO', () => {
      const createUserSpy = mockApiService.userApi.createUser as jasmine.Spy;

      createUserSpy.and.returnValue(of({ data: { id: '123' } }));

      component.submitUserForm();

      expect(createUserSpy).toHaveBeenCalled();

      const calledDto = createUserSpy.calls.argsFor(0)[0];

      expect(calledDto).toEqual({
        firstName: 'Marko',
        lastName: 'Markovic',
        birthDate: new Date('2000-05-15').toISOString(),
        email: 'marko@example.com',
        address: 'Bulevar 10',
        phoneNumber: '+381641234567',
        userStatus: UserStatus.ACTIVE.toString(),
        userRoleId: 1,
        password: 'password123',
      });
    });

    it('should send birthDate as ISO string', () => {
      const createUserSpy = mockApiService.userApi.createUser as jasmine.Spy;

      const testDate = new Date('2000-05-15');

      component.userForm.patchValue({
        birthDate: testDate,
      });

      createUserSpy.and.returnValue(of({ data: {} }));

      component.submitUserForm();

      const calledDto = createUserSpy.calls.argsFor(0)[0];

      expect(calledDto.birthDate).toBe(testDate.toISOString());
    });

    it('should close dialog on successful creation', () => {
      const createUserSpy = mockApiService.userApi.createUser as jasmine.Spy;

      const mockResponse = { data: { id: '123' } };

      createUserSpy.and.returnValue(of(mockResponse));

      component.submitUserForm();

      expect(mockDialogRef.close).toHaveBeenCalledWith(mockResponse.data);
    });

    it('should show snackbar on error', () => {
      const createUserSpy = mockApiService.userApi.createUser as jasmine.Spy;

      createUserSpy.and.returnValue(throwError(() => 'Error creating user'));

      component.submitUserForm();

      expect(mockSnackBar.open).toHaveBeenCalled();
    });

    it('should not call API if userRole is missing', () => {
      const createUserSpy = mockApiService.userApi.createUser as jasmine.Spy;

      component.userForm.patchValue({
        userRole: null,
      });

      component.submitUserForm();

      expect(createUserSpy).not.toHaveBeenCalled();
    });
  });

  describe('cancel', () => {
    it('should close dialog without data', () => {
      component.cancel();

      expect(mockDialogRef.close).toHaveBeenCalled();
    });
  });

  describe('invalid form submission', () => {
    it('should not call API if form is invalid', () => {
      const createUserSpy = mockApiService.userApi.createUser as jasmine.Spy;

      component.userForm.patchValue({
        firstName: '', 
      });

      component.submitUserForm();

      expect(createUserSpy).not.toHaveBeenCalled();
    });
  });
});
