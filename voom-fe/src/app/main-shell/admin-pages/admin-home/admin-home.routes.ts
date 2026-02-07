import {Route} from '@angular/router';
import {AdminLive, ROUTE_ADMIN_LIVE} from './admin-live/admin-live';
import {AdminActivity, ROUTE_ADMIN_ACTIVITY} from './admin-activity/admin-activity';
import {AdminUsers, ROUTE_ADMIN_USERS} from './admin-users/admin-users';
import {AdminPricing, ROUTE_ADMIN_PRICING} from './admin-pricing/admin-pricing';
import {AdminVehicles, ROUTE_ADMIN_VEHICLES} from './admin-vehicles/admin-vehicles';
import { ROUTE_ADMIN_REPORT, AdminReport } from '../admin-report/admin-report';
import { AdminTracking, ROUTE_ADMIN_TRACKING } from './admin-tracking/admin-tracking';
import { AdminChat, ROUTE_ADMIN_CHAT } from './admin-chat/admin-chat';

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
    path: ROUTE_ADMIN_REPORT,
    component: AdminReport,
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
    path: ROUTE_ADMIN_TRACKING,
    component: AdminTracking,
  },
  {
    path: ROUTE_ADMIN_CHAT,
    component: AdminChat,
  },
  {
    path: "**",
    redirectTo: ROUTE_ADMIN_LIVE,
  }
] satisfies Route[];
