import {Component, inject} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import ApiService from '../../../../../shared/rest/api-service';

@Component({
  selector: 'app-admin-users-dialog',
  imports: [
    MatButton
  ],
  templateUrl: './admin-users-dialog.html',
  styleUrl: './admin-users-dialog.css',
})
export class AdminUsersDialog {

  private apiService = inject(ApiService);

  constructor(private dialogRef: MatDialogRef<AdminUsersDialog>) {
  }

  cancel() {
    this.dialogRef.close();
  }

  submit() {
    this.dialogRef.close();
    // this.apiService.userApi.
  }
}
