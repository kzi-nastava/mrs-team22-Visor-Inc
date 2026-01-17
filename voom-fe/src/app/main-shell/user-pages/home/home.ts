import {AfterViewInit, Component, computed, signal, ViewChild} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Map} from '../../../shared/map/map';
import {Dropdown} from '../../../shared/dropdown/dropdown';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';
import {MatButton} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {DriverSummaryDto, PREDEFINED_ROUTES, RideApi, RideRequestDto, ScheduledRideDto} from './home.api';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {DriverSimulationWsService} from '../../../shared/websocket/DriverSimulationWsService';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {FavoriteRouteNameDialog} from '../favorite-routes/favorite-route-name-dialog/favorite-route-name-dialog';
import {FavoriteRouteDto} from '../favorite-routes/favorite-routes.api';
import ApiService from '../../../shared/rest/api-service';


export const ROUTE_USER_HOME = 'user/home';

type RoutePointType = 'PICKUP' | 'STOP' | 'DROPOFF';

type ScheduleType = 'NOW' | 'LATER';

export interface RoutePoint {
  id: string;
  lat: number;
  lng: number;
  address: string;
  type: RoutePointType;
  order: number;
}

@Component({
  selector: 'app-home',
  imports: [
    Map,
    Dropdown,
    ValueInputString,
    MatButton,
    ReactiveFormsModule,
    MatIconModule,
    MatTooltipModule,
    MatCheckboxModule,
    MatSnackBarModule,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class UserHome implements AfterViewInit {
  @ViewChild(Map) map!: Map;

  constructor(
    private rideApi: RideApi,
    private snackBar: MatSnackBar,
    private driverSocket: DriverSimulationWsService,
    private router: Router,
    private dialog: MatDialog,
    private apiService: ApiService
  ) {}

  routePoints = signal<RoutePoint[]>([]);
  passengerEmails = signal<string[]>([]);

  selectedVehicle: number | null = null;
  selectedTime: ScheduleType = 'NOW';
  scheduledRide = signal<ScheduledRideDto | null>(null);
  isRideLocked = signal<boolean>(false);
  scheduledDriverSent = signal<boolean>(false);

  drivers: number[] = [];

  renderedDrivers: number[] = [];


  rideForm = new FormGroup({
    pickup: new FormControl<string>(''),
    dropoff: new FormControl<string>(''),
    emailInput: new FormControl<string>('', Validators.email),
    pets: new FormControl<boolean>(false),
    baby: new FormControl<boolean>(false),
    scheduledTime: new FormControl<string | null>(null),
  });

  vehicleOptions = [
    { label: 'Standard', value: 1 },
    { label: 'Luxury', value: 3 },
    { label: 'Van', value: 2 },
  ];

  timeOptions = [
    { label: 'Now', value: 'NOW' },
    { label: 'Later', value: 'LATER' },
  ];

  pitstopsView = computed(() =>
    this.routePoints()
      .filter((p) => p.type === 'STOP')
      .map((p) => ({
        ...p,
        cleanAddress: p.address.replace(/\s*,?\s*Novi Sad.*$/i, '').trim(),
      }))
  );

  get isLaterSelected(): boolean {
    return this.selectedTime === 'LATER';
  }

  get minTime(): string {
    const now = new Date();
    return now.toTimeString().slice(0, 5);
  }

  get maxTime(): string {
    const max = new Date();
    max.setHours(max.getHours() + 5);
    return max.toTimeString().slice(0, 5);
  }

  get isLocked(): boolean {
    return this.isRideLocked();
  }

  private handleScheduledRides(rides: ScheduledRideDto[]) {
    if (!rides || rides.length === 0) return;

    const now = Date.now() - 60 * 60 * 1000; // one hour back to account for server-client time diff
    const TEN_MIN = 10 * 60 * 1000;

    const ride = rides
      .map((r) => ({
        ...r,
        startMs: new Date(r.scheduledStartTime).getTime(),
      }))
      .filter((r) => {
        const diff = r.startMs - now;
        return diff >= 0 && diff <= TEN_MIN;
      })
      .sort((a, b) => a.startMs - b.startMs)[0];

    if (!ride) return;

    if (this.scheduledRide()?.rideId === ride.rideId) return;

    this.scheduledRide.set(ride);
    this.isRideLocked.set(true);

    this.routePoints.set(
      ride.route
        .sort((a, b) => a.order - b.order)
        .map((p) => ({
          id: crypto.randomUUID(),
          lat: p.lat,
          lng: p.lng,
          address: '',
          type: p.type,
          order: p.order,
        }))
    );

    this.rideForm.patchValue({
      pickup: ride.route.find((p) => p.type === 'PICKUP')?.address || '',
      dropoff: ride.route.find((p) => p.type === 'DROPOFF')?.address || '',
    });

    if (ride.driverId && !this.scheduledDriverSent()) {
      const pickup = ride.route.find((p) => p.type === 'PICKUP');
      if (pickup) {
        this.sendDriverToPickup(ride.driverId, pickup.lat, pickup.lng);
        this.scheduledDriverSent.set(true);
      }
    }

    if (this.isRideLocked()) {
      this.rideForm.disable({ emitEvent: false });
    } else {
      this.rideForm.enable({ emitEvent: false });
    }

    this.snackBar.open('You have a scheduled ride starting soon', 'Close', { duration: 4000 });
  }

  isScheduledTimeValid(): boolean {
    const value = this.rideForm.value.scheduledTime;
    if (!value) return false;

    const now = new Date();
    const selected = new Date();
    const [h, m] = value.split(':').map(Number);

    selected.setHours(h, m, 0, 0);

    const diffMinutes = (selected.getTime() - now.getTime()) / 60000;
    return diffMinutes >= 0 && diffMinutes <= 300;
  }

  onMapClick(event: { lat: number; lng: number; address: string }) {
    if (this.isLocked) return;

    const cleanAddress = event.address.replace(/\s*,?\s*Novi Sad.*$/i, '').trim();
    const points = this.routePoints();

    if (points.length === 0) {
      this.routePoints.set([
        {
          id: crypto.randomUUID(),
          lat: event.lat,
          lng: event.lng,
          address: cleanAddress,
          type: 'PICKUP',
          order: 0,
        },
      ]);
      this.rideForm.patchValue({ pickup: cleanAddress });
      return;
    }

    const updated = points.map((p) =>
      p.type === 'DROPOFF' ? { ...p, type: 'STOP' as RoutePointType } : p
    );

    updated.push({
      id: crypto.randomUUID(),
      lat: event.lat,
      lng: event.lng,
      address: cleanAddress,
      type: 'DROPOFF',
      order: updated.length,
    });

    this.routePoints.set(updated);
    this.rideForm.patchValue({ dropoff: cleanAddress });
  }

  setAsDropoff(id: string) {
    const points = this.routePoints();
    const pickup = points.find((p) => p.type === 'PICKUP');
    const newDropoff = points.find((p) => p.id === id);
    if (!pickup || !newDropoff) return;

    const stops = points.filter((p) => p.id !== id && p.type !== 'PICKUP');

    const updated: RoutePoint[] = [
      { ...pickup, order: 0 },
      ...stops.map((p, i) => ({ ...p, type: 'STOP' as RoutePointType, order: i + 1 })),
      { ...newDropoff, type: 'DROPOFF', order: stops.length + 1 },
    ];

    this.routePoints.set(updated);
    this.rideForm.patchValue({ dropoff: newDropoff.address });
  }

  removePoint(id: string) {
    this.routePoints.set(
      this.routePoints()
        .filter((p) => p.id !== id)
        .map((p, i) => ({ ...p, order: i }))
    );
  }

  get emailControl() {
    return this.rideForm.get('emailInput');
  }

  onMapCleared() {
    this.routePoints.set([]);
    this.passengerEmails.set([]);
    this.rideForm.reset({
      pickup: '',
      dropoff: '',
      pets: false,
      baby: false,
      scheduledTime: null,
    });
  }

  addEmail() {
    const control = this.emailControl;
    const email = control?.value?.trim();
    if (!email || control?.invalid) return;
    if (this.passengerEmails().length >= 3) return;
    if (this.passengerEmails().includes(email)) return;

    this.passengerEmails.set([...this.passengerEmails(), email]);
    control?.reset('');
  }

  removeEmail(email: string) {
    this.passengerEmails.set(this.passengerEmails().filter((e) => e !== email));
  }

  private buildScheduledDate(): string {
    const [h, m] = this.rideForm.value.scheduledTime!.split(':').map(Number);
    const date = new Date();
    date.setHours(h, m, 0, 0);
    return date.toISOString();
  }

  sendDriverToPickup(driverId: number, lat: number, lng: number) {
    const driver = this.map.getDriver(driverId);
    if (!driver) return;

    driver.status = 'GOING_TO_PICKUP';

    this.driverSocket.requestRoute({
      driverId,
      start: driver.marker.getLatLng(),
      end: { lat, lng },
    });
  }

  confirmRide() {
    if (this.selectedVehicle === null) {
      this.snackBar.open('Please select vehicle type', 'Close', {
        duration: 3000,
      });
      return;
    }

    const schedule: { type: ScheduleType; startAt: string } =
      this.selectedTime === 'LATER'
        ? { type: 'LATER', startAt: this.buildScheduledDate() }
        : { type: 'NOW', startAt: new Date().toISOString() };

    const freeDriversSnapshot = this.map.getFreeDriversSnapshot();

    console.log('Free drivers snapshot:', freeDriversSnapshot);

    const payload: RideRequestDto = {
      route: {
        points: this.routePoints().map((p) => ({
          lat: p.lat,
          lng: p.lng,
          orderIndex: p.order,
          type: p.type,
          address: p.address,
        })),
      },
      schedule,
      vehicleTypeId: this.selectedVehicle,
      preferences: {
        pets: !!this.rideForm.value.pets,
        baby: !!this.rideForm.value.baby,
      },
      linkedPassengers: this.passengerEmails(),
      freeDriversSnapshot: freeDriversSnapshot,
    };

    this.rideApi.createRideRequest(payload).subscribe({
      next: (res) => {
        if (res.status === 'ACCEPTED' && res.driver) {
          this.snackBar.open(
            `Ride accepted. Price: ${res.price}, Driver: ${res.driver?.firstName} ${res.driver?.lastName}`,
            'Close',
            { duration: 4000 }
          );
          if (res.pickupLat && res.pickupLng && res.scheduledTime === null) {
            this.sendDriverToPickup(res.driver.id, res.pickupLat, res.pickupLng);
          }
          this.isRideLocked.set(true);
          this.rideForm.disable({ emitEvent: false });
        } else {
          this.snackBar.open('No drivers available. Ride rejected.', 'Close', { duration: 4000 });
        }
      },
      error: () => {
        this.snackBar.open('Failed to create ride request', 'Close', { duration: 4000 });
      },
    });
  }

  addRouteToFavorites() {
    const points = this.routePoints();

    if (points.length < 2) {
      this.snackBar.open('Pickup and dropoff are required', 'Close', { duration: 3000 });
      return;
    }

    const dialogRef = this.dialog.open(FavoriteRouteNameDialog, {
      width: '360px',
    });

    dialogRef.afterClosed().subscribe((name: string | undefined) => {
      if (!name) return;

      const payload = {
        name,
        points: points.map((p) => ({
          lat: p.lat,
          lng: p.lng,
          orderIndex: p.order,
          type: p.type,
          address: p.address,
        })),
      };

      this.rideApi.createFavoriteRoute(payload).subscribe({
        next: () => {
          this.snackBar.open('Route added to favorites', 'Close', { duration: 3000 });
        },
        error: (err) => {
          if (err.status === 409) {
            this.snackBar.open('Route already exists', 'Close', { duration: 3000 });
          } else {
            this.snackBar.open('Failed to save route', 'Close', { duration: 3000 });
          }
        },
      });
    });
  }

    loadActiveDrivers() {
      this.apiService.rideApi.getActiveDrivers().subscribe((res) => {
        const drivers: DriverSummaryDto[] = res.data ?? [];
        if (drivers.length === 0) return;
        
        console.log('Loaded active drivers:', drivers);

        this.driverSocket.connect(
          (route) => {
          this.map.applyDriverRoute(route.driverId, route.route);
        },
          (scheduledRides) => {
            this.handleScheduledRides(scheduledRides);
        },
          undefined,
          (pos) => {
            if (!this.drivers.includes(pos.driverId)) {
              this.drivers.push(pos.driverId);
              const name = drivers.filter((d) => d.id === pos.driverId).at(0)?.firstName ?? '';
              const lastname = drivers.filter((d) => d.id === pos.driverId).at(0)?.lastName ?? '';
              const status = drivers.filter((d) => d.id === pos.driverId).at(0)?.status;
              this.map.addSimulatedDriver({
                id: pos.driverId,
                firstName: name,
                lastName: lastname,
                start: {
                  lat: pos.lat,
                  lng: pos.lng,
                },
                status: status as any || 'FREE',
              });
            } else {
              this.map.updateDriverPosition(pos.driverId, pos.lat, pos.lng);
            }
          }
        );
      });
  }
  


  private applyFavoriteRoute(route: FavoriteRouteDto) {
    const points = route.points
      .slice()
      .sort((a, b) => a.orderIndex - b.orderIndex)
      .map((p) => ({
        id: crypto.randomUUID(),
        lat: p.lat,
        lng: p.lng,
        address: p.address,
        type: p.type,
        order: p.orderIndex,
      }));

    this.routePoints.set(points);

    const pickup = points.find((p) => p.type === 'PICKUP');
    const dropoff = points.find((p) => p.type === 'DROPOFF');

    this.rideForm.patchValue({
      pickup: pickup?.address ?? '',
      dropoff: dropoff?.address ?? '',
    });
  }

  

  openFavoriteRoutes() {
    this.router.navigate(['/user/favorite-routes']);
  }

  ngAfterViewInit() {
    const favoriteRoute = history.state?.favoriteRoute;

    this.loadActiveDrivers();

    if (favoriteRoute) {
      this.applyFavoriteRoute(favoriteRoute);

      history.replaceState({ ...history.state, favoriteRoute: undefined }, document.title);
    }

    
  }
}
