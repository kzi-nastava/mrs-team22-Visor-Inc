import {Route} from '@angular/router';
import {DriverPages} from './driver-pages/driver-pages';
import {UserPages} from './user-pages/user-pages';
import {AdminPages} from './admin-pages/admin-pages';
import {roleGuard} from '../shared/guards/role-guard';


export default [
  {
    path: '',
    component: UserPages,
    canActivate: [roleGuard],
    loadChildren: () => import('./user-pages/user-pages.routes'),
    data: { roles: ['USER'] },
  },
  {
    path: '',
    component: DriverPages,
    canActivate: [roleGuard],
    loadChildren: () => import('./driver-pages/driver-pages.routes'),
    data: { roles: ['DRIVER'] },
  },
  {
    path: '',
    component: AdminPages,
    canActivate: [roleGuard],
    loadChildren: () => import('./admin-pages/admin-pages.routes'),
    data: { roles: ['ADMIN'] },
  },
  {
    path: "**",
    redirectTo: '',
  }
] satisfies Route[];
