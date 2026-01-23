import { Component, computed, inject, signal } from '@angular/core';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {MatDivider} from '@angular/material/list';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ValueInputDate} from '../../../../shared/value-input/value-input-date/value-input-date';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';
import { BehaviorSubject, filter, map, merge, scan, startWith } from 'rxjs';
import ApiService from '../../../../shared/rest/api-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import {UserProfileDto, UserStatus} from '../../../../shared/rest/user/user.model';
import {MatDialog} from '@angular/material/dialog';
import {AdminUsersDialog} from './admin-users-dialog/admin-users-dialog';
import { VehicleTypeDto } from '../../../../shared/rest/vehicle/vehicle-type.model';

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

  searchFormControl = new FormControl<string>('');
  searchTerm = toSignal(this.searchFormControl.valueChanges.pipe(startWith(this.searchFormControl.getRawValue())));

  private apiService = inject(ApiService);
  private snackBar = inject(MatSnackBar);

  constructor(private dialog: MatDialog) {
  }

  initialUsers$ = this.apiService.userApi.getUsers().pipe(
    map(response => response.data),
  );

  userCreate$ = new BehaviorSubject<UserProfileDto | null>(null);
  userUpdate$ = new BehaviorSubject<UserProfileDto | null>(null);

  users$= merge(
    this.initialUsers$.pipe(filter(user => !!user), map(response => { return {type:'initial', value: response} })),
    this.userCreate$.asObservable().pipe(takeUntilDestroyed(), filter(user => !!user), map(response => { return {type:'create', value: [response]} })),
    this.userUpdate$.asObservable().pipe(takeUntilDestroyed(), filter(user => !!user), map(response => { return {type:'update', value: [response]} })),
  ).pipe(
    takeUntilDestroyed(),
    scan((acc, obj) => {
      switch (obj.type) {
        case 'initial':
          return obj.value;
        case 'create':
          return [...acc, ...obj.value];
        case 'update':
          return [...acc.filter(user => user.id !== obj.value[0].id), obj.value[0]];
        default:
          return [];
      }
    }, [] as UserProfileDto[]),
  );

  userRoles$ = this.apiService.userRoleApi.getUserRoles().pipe(
    map(response => response.data),
  );

  users = toSignal(this.users$);

  filteredUsers = computed(() => {
    const searchTerm = this.searchTerm();
    const users = this.users();

    if (!users || !searchTerm || !searchTerm.length) {
      return users;
    }

    return users.filter(user => user.firstName.toLowerCase().includes(searchTerm.toLowerCase()) || user.lastName.toLowerCase().includes(searchTerm.toLowerCase()));
  });

  userRoles = toSignal(this.userRoles$);
  selectedUser = signal<UserProfileDto | null>(null);

  protected saveGeneralInfo() {
    const user = this.selectedUser();

    if (!user) {
      return;
    }

    const updatedUserDto: UserProfileDto = {
      id: user.id,
      firstName: this.userGeneralForm.value.firstName!,
      lastName: this.userGeneralForm.value.lastName!,
      birthDate: this.userGeneralForm.value.birthDate!.toISOString(),
      email: this.userGeneralForm.value.email!,
      address: this.userGeneralForm.value.address!,
      phoneNumber: this.userGeneralForm.value.phoneNumber!,
      userStatus: 'INACTIVE',
      pfpUrl: null,
      userRoleId: user.userRoleId,
    }

    this.apiService.userApi.updateUser(user.id!, updatedUserDto).pipe(
      map(response => response.data),
    ).subscribe((user) => {
      if (user) {
        this.snackBar.open("User updated successfully", '', {horizontalPosition: "right", duration : 3000});
        this.userUpdate$.next(user);
      } else {
        this.snackBar.open("User update failed",'', {horizontalPosition: "right", duration : 3000});
      }
    });
  }

  protected selectUser(user: UserProfileDto) {
    this.selectedUser.set(user);
    this.userGeneralForm.patchValue({
      firstName: user.firstName,
      lastName: user.lastName,
      birthDate: user.birthDate ? new Date(user.birthDate) : null,
      email: user.email,
      address: user.address,
      phoneNumber: user.phoneNumber,
    });
 }

  protected addUser() {
    this.dialog.open(AdminUsersDialog).afterClosed().subscribe((user) => {
      if (user) {
        this.snackBar.open("User added successfully", '', {horizontalPosition: "right", duration : 3000});
        this.userCreate$.next(user);
      } else {
        this.snackBar.open("User add failed",'', {horizontalPosition: "right", duration : 3000});
      }
    })
  }

  protected deleteUser() {

  }
}
