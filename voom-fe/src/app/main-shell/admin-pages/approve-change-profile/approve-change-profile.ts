import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { AdminVehicleRequestsApi } from './approve-change-profile.api';
import { DriverVehicleChangeRequestDto } from './approve-change-profile.api';

@Component({
  selector: 'app-approve-change-profile',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatSnackBarModule],
  templateUrl: './approve-change-profile.html',
})
export class ApproveChangeProfile {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private api = inject(AdminVehicleRequestsApi);

  requestId!: string;

  request = signal<DriverVehicleChangeRequestDto | null>(null);
  loading = signal(true);

  ngOnInit() {
    this.requestId = this.route.snapshot.paramMap.get('id')!;

    this.api.getRequest(this.requestId).subscribe({
      next: (res) => {
        this.request.set(res.data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.snackBar.open('Request not found', 'Dismiss', { duration: 3000 });
      },
    });
  }

  approve() {
    this.api.approve(this.requestId).subscribe({
      next: () => {
        this.snackBar.open('Request approved', 'OK', { duration: 2000 });
        setTimeout(() => this.router.navigateByUrl('/admin'), 500);
      },
      error: () => {
        this.snackBar.open('Approval failed', 'Dismiss', { duration: 3000 });
      },
    });
  }

  reject() {
    this.api.reject(this.requestId).subscribe({
      next: () => {
        this.snackBar.open('Request rejected', 'OK', { duration: 2000 });
        setTimeout(() => this.router.navigateByUrl('/admin'), 500);
      },
      error: () => {
        this.snackBar.open('Rejection failed', 'Dismiss', { duration: 3000 });
      },
    });
  }
}
