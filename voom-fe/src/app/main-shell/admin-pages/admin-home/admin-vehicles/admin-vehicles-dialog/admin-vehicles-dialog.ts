import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import ApiService from '../../../../../shared/rest/api-service';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-admin-vehicles-dialog',
    imports: [
        MatButton
    ],
  templateUrl: './admin-vehicles-dialog.html',
  styleUrl: './admin-vehicles-dialog.css',
})
export class AdminVehiclesDialog {

  private apiService = inject(ApiService);

  constructor(private dialogRef: MatDialogRef<AdminVehiclesDialog>) {
  }

  protected submit() {
    this.dialogRef.close();
  }

  protected cancel() {
    this.dialogRef.close();
  }
}
