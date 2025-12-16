import { Component, signal } from '@angular/core';
import { Map, RouteSummary } from '../map/map';

@Component({
  selector: 'app-ride-form',
  imports: [Map],
  templateUrl: './ride-form.html',
  styleUrl: './ride-form.css',
})
export class RideForm {
  public estimateMsg = signal('Enter pickup, dropoff and type to see your estimate');
  public sourceField = signal('');
  public destField = signal('');
  public typeValue = signal(100);
  public calculatedPrice = signal(0);
  public distance = signal(0);
  public eta = signal(0);

  onRouteCalculated(routeSummary: RouteSummary): void {
    if (routeSummary) {
      this.calculatedPrice.set(Math.round(this.typeValue() + this.distance() * 120));
      this.distance.set(routeSummary.distance);
      this.eta.set(Math.round(routeSummary.time));
      this.estimateMsg.set(
        `Price for estimated distance of ${this.distance()}km is ${this.calculatedPrice()} with ETA in ${this.eta()} minutes.`
      );
      this.sourceField.set(routeSummary.sourceAddress);
      this.destField.set(routeSummary.destinationAddress);
    }
  }

  onTypeChange(event: Event) {
    const value = Number((event.target as HTMLSelectElement).value);
    this.calculatedPrice.set(Math.round(value + this.distance() * 120));
    this.estimateMsg.set(
      `Price for estimated distance of ${this.distance()}km is ${this.calculatedPrice()} with ETA in ${this.eta()} minutes.`
    );
    this.typeValue.set(value);
  }
}
