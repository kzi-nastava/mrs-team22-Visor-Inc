import {Route} from '@angular/router';
import {ROUTE_USER_PROFILE, UserProfile} from './user-profile/user-profile';
import {ROUTE_USER_HOME, UserHome} from './home/home';
import {FavoriteRoutes, ROUTE_FAVORITE_ROUTES} from './favorite-routes/favorite-routes';

export default [
  {
    path: ROUTE_USER_HOME,
    component: UserHome,
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

] satisfies Route[]
