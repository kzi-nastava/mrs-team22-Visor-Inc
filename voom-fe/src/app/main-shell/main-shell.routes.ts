import {Route} from '@angular/router';
import {DriverPages, ROUTE_DRIVER_PAGES} from './driver-pages/driver-pages';
import {ROUTE_USER_PAGES, UserPages} from './user-pages/user-pages';
import {AdminPages, ROUTE_ADMIN_PAGES} from './admin-pages/admin-pages';

export default [
  {
    path: ROUTE_USER_PAGES,
    component: UserPages,
    loadChildren: () => import('./user-pages/user-pages.routes'),
  },
  {
    path: ROUTE_DRIVER_PAGES,
    component: DriverPages,
    loadChildren: () => import('./driver-pages/driver-pages.routes'),
  },
  {
    path: ROUTE_ADMIN_PAGES,
    component: AdminPages,
    loadChildren: () => import('./admin-pages/admin-pages.routes'),
  },
  {
    path: "**",
    redirectTo: ROUTE_USER_PAGES,
  }
] satisfies Route[];
