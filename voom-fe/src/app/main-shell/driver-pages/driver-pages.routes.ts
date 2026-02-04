import {Route} from "@angular/router";
import {DriverRideHistory, ROUTE_DRIVER_RIDE_HISTORY} from './ride-history/driver-ride-history';
import {DriverHome, ROUTE_DRIVER_HOME} from './driver-home/driver-home';
import {DriverPages} from './driver-pages';
import { ROUTE_USER_PROFILE, UserProfile } from "../user-pages/user-profile/user-profile";
import { ROUTE_SCHEDULED_RIDES, ScheduledRides } from "./scheduled-rides/scheduled-rides";
import { ROUTE_STATISTICS, Report } from "../../shared/report/report";

export default [
  {
    path: '',
    component: DriverPages,
    children: [
      {
        path: ROUTE_SCHEDULED_RIDES,
        component: ScheduledRides,
      },
      {
        path: ROUTE_STATISTICS,
        component: Report,
      },
      {
        path: ROUTE_DRIVER_HOME,
        component: DriverHome,
      },
      {
        path: ROUTE_DRIVER_RIDE_HISTORY,
        component: DriverRideHistory,
      },
      {
        path: ROUTE_USER_PROFILE,
        component: UserProfile,
      },
      {
        path: '**',
        redirectTo: ROUTE_DRIVER_HOME,
      }
    ]
  },
  {
    path: '**',
    redirectTo: '',
  }
] satisfies Route[];
