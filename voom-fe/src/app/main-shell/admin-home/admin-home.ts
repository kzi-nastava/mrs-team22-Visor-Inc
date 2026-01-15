import {Component} from '@angular/core';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {Router, RouterOutlet} from '@angular/router';
import {MatIcon} from '@angular/material/icon';
import {Tab} from './tab';
import {ROUTE_ADMIN_LIVE} from './admin-live/admin-live';
import {ROUTE_ADMIN_ACTIVITY} from './admin-activity/admin-activity';
import {MatRipple} from '@angular/material/core';

export const ROUTE_HOME_ADMIN = "admin-home"

@Component({
  selector: 'app-admin-home',
  imports: [
    MatDrawerContainer,
    MatDrawer,
    MatDrawerContent,
    RouterOutlet,
    MatIcon,
    MatRipple
  ],
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
      icon: 'article',
      title: 'Activity',
      routerLink: ROUTE_ADMIN_ACTIVITY,
    },
    {
      icon: 'groups',
      title: 'Users',
      routerLink: 'users',
    },
    {
      icon: 'local_taxi',
      title: 'Drivers',
      routerLink: 'drivers',
    },
    {
      icon: 'attach_money',
      title: 'Pricing',
      routerLink: 'pricing',
    },
    {
      icon: 'manage_accounts',
      title: 'Roles',
      routerLink: 'roles',
    },
  ];

  constructor(private router: Router) {
  }


  protected changeTab(routerLink: string) {
    this.router.navigate([ROUTE_HOME_ADMIN, routerLink]);
  }
}
