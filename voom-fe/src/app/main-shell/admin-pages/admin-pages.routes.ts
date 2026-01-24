import {
  AdminRegisterDriver,
  ROUTE_ADMIN_REGISTER_DRIVER,
} from './register-driver/register-driver';
import { Route } from '@angular/router';
import {
  ActivateProfile,
  ROUTE_ACTIVATE_PROFILE,
} from '../../unauthenticated/activate/activate-profile/activate-profile';
import { AdminPages } from './admin-pages';
import { AdminHome, ROUTE_ADMIN_HOME } from './admin-home/admin-home';
import { ApproveChangeProfile } from './approve-change-profile/approve-change-profile';

export default [
  {
    path: '',
    component: AdminPages,
    children: [
      {
        path: ROUTE_ADMIN_HOME,
        component: AdminHome,
        loadChildren: () => import('./admin-home/admin-home.routes'),
      },
      {
        path: ROUTE_ADMIN_REGISTER_DRIVER,
        component: AdminRegisterDriver,
      },
      {
        path: ROUTE_ACTIVATE_PROFILE,
        component: ActivateProfile,
      },
      {
        path: 'vehicle-requests/:id',
        component: ApproveChangeProfile,
      },
      {
        path: '**',
        redirectTo: ROUTE_ADMIN_HOME,
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
] satisfies Route[];
