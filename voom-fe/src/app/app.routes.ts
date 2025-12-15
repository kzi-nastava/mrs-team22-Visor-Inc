import { Routes } from '@angular/router';

export const routes: Routes = [
    { path: '', loadComponent: () => import('./map/map').then(m => m.Map) },
    { path: 'profile', loadComponent: () => import('./pages/user-profile/user-profile').then(m => m.UserProfile) },
];
