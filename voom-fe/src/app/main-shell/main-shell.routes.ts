import {Route} from '@angular/router';
import {DriverPages} from './driver-pages/driver-pages';
import {UserPages} from './user-pages/user-pages';
import {AdminPages} from './admin-pages/admin-pages';


export default [
  {
    path: '',
    component: UserPages,
    loadChildren: () => import('./user-pages/user-pages.routes'),
    data: { roles: ['USER'] },
  },
  {
    path: '',
    component: DriverPages,
    loadChildren: () => import('./driver-pages/driver-pages.routes'),
    data: { roles: ['DRIVER'] },
  },
  {
    path: '',
    component: AdminPages,
    loadChildren: () => import('./admin-pages/admin-pages.routes'),
    data: { roles: ['DRIVER'] },
  },
  {
    path: "**",
    redirectTo: '',
  }

] satisfies Route[];
