import {Route} from '@angular/router';
import {AdminLive, ROUTE_ADMIN_LIVE} from './admin-live/admin-live';
import {AdminActivity, ROUTE_ADMIN_ACTIVITY} from './admin-activity/admin-activity';
import {AdminUsers, ROUTE_ADMIN_USERS} from './admin-users/admin-users';
import {AdminPricing, ROUTE_ADMIN_PRICING} from './admin-pricing/admin-pricing';
import {AdminVehicles, ROUTE_ADMIN_VEHICLES} from './admin-vehicles/admin-vehicles';

export default [
  {
    path: ROUTE_ADMIN_LIVE,
    component: AdminLive,
  },
  {
    path: ROUTE_ADMIN_ACTIVITY,
    component: AdminActivity,
  },
  {
    path: ROUTE_ADMIN_USERS,
    component: AdminUsers,
  },
  {
    path: ROUTE_ADMIN_VEHICLES,
    component: AdminVehicles,
  },
  {
    path: ROUTE_ADMIN_PRICING,
    component: AdminPricing,
  },
  {
    path: "**",
    redirectTo: ROUTE_ADMIN_LIVE,
  }
] satisfies Route[];
