import { Component } from '@angular/core';
import { MatDrawer, MatDrawerContainer, MatDrawerContent } from '@angular/material/sidenav';
import { Router, RouterOutlet } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import { Tab } from './tab';
import { ROUTE_ADMIN_LIVE } from './admin-live/admin-live';
import { ROUTE_ADMIN_ACTIVITY } from './admin-activity/admin-activity';
import { MatRipple } from '@angular/material/core';
import { ROUTE_ADMIN_USERS } from './admin-users/admin-users';
import { ROUTE_ADMIN_PRICING } from './admin-pricing/admin-pricing';
import { ROUTE_ADMIN_PAGES } from '../admin-pages';
import { ROUTE_ADMIN_VEHICLES } from './admin-vehicles/admin-vehicles';
import { ROUTE_ADMIN_REPORT } from '../admin-report/admin-report';
import { ROUTE_ADMIN_TRACKING } from './admin-tracking/admin-tracking';

export const ROUTE_ADMIN_HOME = 'ride';

@Component({
  selector: 'app-admin-ride',
  imports: [MatDrawerContainer, MatDrawer, MatDrawerContent, RouterOutlet, MatIcon, MatRipple],
  templateUrl: './admin-home.html',
  styleUrl: './admin-home.css',
})
export class AdminHome {
  protected tabs: Tab[] = [
    {
      icon: 'sensors',
      title: 'Live',
      routerLink: ROUTE_ADMIN_LIVE,
    },
    {
      icon: 'directions_car',
      title: 'Tracking',
      routerLink: ROUTE_ADMIN_TRACKING,
    },
    {
      icon: 'article',
      title: 'Activity',
      routerLink: ROUTE_ADMIN_ACTIVITY,
    },
    {
      icon: 'groups',
      title: 'Users',
      routerLink: ROUTE_ADMIN_USERS,
    },
    {
      icon: 'local_taxi',
      title: 'Vehicles',
      routerLink: ROUTE_ADMIN_VEHICLES,
    },
    {
      icon: 'attach_money',
      title: 'Pricing',
      routerLink: ROUTE_ADMIN_PRICING,
    },
    {
      icon: 'bar_chart',
      title: 'Reports',
      routerLink: ROUTE_ADMIN_REPORT,
    },
  ];

  constructor(private router: Router) {}

  protected changeTab(routerLink: string) {
    this.router.navigate([ROUTE_ADMIN_PAGES, ROUTE_ADMIN_HOME, routerLink]);
  }
}
