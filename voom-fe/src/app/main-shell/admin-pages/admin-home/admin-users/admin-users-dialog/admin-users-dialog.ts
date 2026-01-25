import { Component, Inject, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';
import ApiService from '../../../../../shared/rest/api-service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, map, of } from 'rxjs';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserRoleDto } from '../../../../../shared/rest/user/user-role.model';
import { MatFormField, MatLabel } from '@angular/material/input';
import { MatOption } from '@angular/material/core';
import { MatSelect } from '@angular/material/select';
import { ValueInputString } from '../../../../../shared/value-input/value-input-string/value-input-string';
import { CreateUserDto, UserStatus } from '../../../../../shared/rest/user/user.model';
import { ValueInputDate } from '../../../../../shared/value-input/value-input-date/value-input-date';

@Component({
  selector: 'app-admin-users-dialog',
  imports: [
    MatButton,
    FormsModule,
    MatFormField,
    MatLabel,
    MatOption,
    MatSelect,
    ReactiveFormsModule,
    ValueInputString,
    ValueInputDate
  ],
  templateUrl: './admin-users-dialog.html',
  styleUrl: './admin-users-dialog.css',
})
export class AdminUsersDialog {

  private apiService = inject(ApiService);
  private snackBar = inject(MatSnackBar);

  userForm = new FormGroup({
    userRole: new FormControl<UserRoleDto | null>({ value: null, disabled: false }, Validators.required),
    userStatus: new FormControl<UserStatus | null>({ value: null, disabled: false }, Validators.required),
    firstName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    lastName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    birthDate: new FormControl<Date | null>(null, [Validators.required]),
    email: new FormControl<string>('', [Validators.required, Validators.email, Validators.maxLength(255)]),
    address: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    phoneNumber: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(55)]),
    password: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]),
  });

  constructor(private dialogRef: MatDialogRef<AdminUsersDialog>, @Inject(MAT_DIALOG_DATA) public data: UserRoleDto[]) {
  }

  cancel() {
    this.dialogRef.close();
  }

  submitUserForm() {
    const formValues = this.userForm.value;

    const userRoleId = formValues.userRole?.id;

    if (!userRoleId) {
      return;
    }

    const dto: CreateUserDto = {
      firstName:  formValues.firstName!,
      lastName:  formValues.lastName!,
      birthDate:  formValues.birthDate!.toISOString(),
      email:  formValues.email!,
      address:  formValues.address!,
      phoneNumber:  formValues.phoneNumber!,
      userStatus: formValues.userStatus!.toString(),
      userRoleId: userRoleId,
      password: formValues.password!,
    }

    this.apiService.userApi.createUser(dto).pipe(
      map(response => response.data),
      catchError(error => {
        this.snackBar.open(error, '', {horizontalPosition: "right", duration : 3000});
        return of(null);
      }),
    ).subscribe((user) => {
      this.dialogRef.close(user);
    });

  }

  protected readonly UserStatus = UserStatus;
  protected readonly Object = Object;
}
