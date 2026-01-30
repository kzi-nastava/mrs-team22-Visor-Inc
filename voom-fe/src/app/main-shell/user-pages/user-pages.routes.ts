import {Route} from '@angular/router';
import {ROUTE_USER_PROFILE, UserProfile} from './user-profile/user-profile';
import {ROUTE_USER_HOME, UserHome} from './user-home/user-home';
import {FavoriteRoutes, ROUTE_FAVORITE_ROUTES} from './favorite-routes/favorite-routes';
import {UserPages} from './user-pages';
import { ROUTE_RIDE_TRACKING, RideTracking } from './ride-tracking/ride-tracking';
import {ROUTE_SCHEDULED_RIDES, ScheduledRides} from './scheduled-rides/scheduled-rides';
import {ROUTE_USER_ACTIVITY, UserActivity} from './user-activity/user-activity';

export default [
  {
    path: '',
    component: UserPages,
    children: [
      {
        path: ROUTE_RIDE_TRACKING,
        component: RideTracking,
      },
      {
        path: ROUTE_USER_HOME,
        component: UserHome,
      },
      {
        path: ROUTE_SCHEDULED_RIDES,
        component: ScheduledRides,
      },
      {
        path: ROUTE_USER_ACTIVITY,
        component: UserActivity,
      },
      {
        path: ROUTE_USER_PROFILE,
        component: UserProfile,
      },
      {
        path: ROUTE_FAVORITE_ROUTES,
        component: FavoriteRoutes,
      },
      {
        path: '**',
        redirectTo: ROUTE_USER_HOME,
      }
    ]
  },
  {
    path: '**',
    redirectTo: '',
  }
] satisfies Route[]
