import {Component, inject, signal} from '@angular/core';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/list';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ValueInputDate} from '../../../../shared/value-input/value-input-date/value-input-date';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';
import {map} from 'rxjs';
import ApiService from '../../../../shared/rest/api-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {toSignal} from '@angular/core/rxjs-interop';
import {UserProfileDto, UserStatus} from '../../../../shared/rest/user/user.model';

export const ROUTE_ADMIN_USERS = "users";

@Component({
  selector: 'app-admin-users',
  imports: [
    MatDrawer,
    MatDrawerContainer,
    MatDrawerContent,
    ValueInputString,
    MatCard,
    MatIcon,
    MatCardContent,
    MatCardTitle,
    MatDivider,
    ValueInputDate,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css',
})
export class AdminUsers {

  userGeneralForm = new FormGroup({
    firstName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    lastName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    birthDate: new FormControl<Date | null>(null, [Validators.required]),
    email: new FormControl<string>('', [Validators.required, Validators.email, Validators.maxLength(255)]),
    address: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    phoneNumber: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(55)]),
  });

  private apiService = inject(ApiService);
  private snackBar = inject(MatSnackBar);

  users$ = this.apiService.userApi.getUsers().pipe(
    map(response => response.data),
  );

  userRoles$ = this.apiService.userRoleApi.getUserRoles().pipe(
    map(response => response.data),
  );

  users = toSignal(this.users$);
  userRoles = toSignal(this.userRoles$);
  selectedUser = signal<UserProfileDto | null>(null);

  protected openProfilePictureDialog() {

  }

  protected saveGeneralInfo() {
    const user = this.selectedUser();

    if (!user) {
      return;
    }

    const updatedUserDto: UserProfileDto = {
      id: user.id,
      firstName: this.userGeneralForm.value.firstName!,
      lastName: this.userGeneralForm.value.lastName!,
      birthDate: this.userGeneralForm.value.birthDate!,
      email: this.userGeneralForm.value.email!,
      address: this.userGeneralForm.value.address!,
      phoneNumber: this.userGeneralForm.value.phoneNumber!,
      userStatus: UserStatus.INACTIVE,
      pfpUrl: null,
      userRoleId: user.userRoleId,
    }

    this.apiService.userApi.updateUser(user.id!, updatedUserDto).pipe(
      map(response => response.data),
    ).subscribe((user) => {
      if (user) {
        this.snackBar.open("User updated successfully");
        this.userGeneralForm.patchValue(user);
        //TODO on update update users$
      } else {
        this.snackBar.open("User update failed");
      }
    });
  }

  protected selectUser(user: UserProfileDto) {
    this.selectedUser.set(user);
    this.userGeneralForm.patchValue(user);
  }

  protected addUser() {

  }

  protected deleteUser() {

  }
}
