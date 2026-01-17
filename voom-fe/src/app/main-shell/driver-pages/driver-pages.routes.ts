import {Route} from "@angular/router";
import {DriverRideHistory, ROUTE_DRIVER_RIDE_HISTORY} from './ride-history/driver-ride-history';
import {DriverHome, ROUTE_DRIVER_HOME} from './driver-home/driver-home';

export default [
  {
    path: ROUTE_DRIVER_HOME,
    component: DriverHome,
  },
  {
    path: ROUTE_DRIVER_RIDE_HISTORY,
    component: DriverRideHistory,
  },
  {
    path: '**',
    redirectTo: ROUTE_DRIVER_HOME,
  }
] satisfies Route[];
