import { Routes } from '@angular/router';
import { ROUTE_METADATA } from './config/route-metadata.config';
import { authGuard, guestGuard } from './guards';

export const routes: Routes = [
  {
    path: 'sign-in',
    loadComponent: () =>
      import('@app/layouts/sign-in-layout/sign-in-layout').then(m => m.SignInLayout),
    title: ROUTE_METADATA.SIGN_IN.title,
    data: {
      meta: ROUTE_METADATA.SIGN_IN,
    },
    canActivate: [guestGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('@pages/sign-in/sign-in').then(m => m.SignIn),
      },
    ],
  },
  {
    path: 'sign-up',
    loadComponent: () =>
      import('@app/layouts/sign-in-layout/sign-in-layout').then(m => m.SignInLayout),
    title: ROUTE_METADATA.SIGN_UP.title,
    data: {
      meta: ROUTE_METADATA.SIGN_UP,
    },
    canActivate: [guestGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('@pages/sign-up/sign-up').then(m => m.SignUp),
      },
    ],
  },
  {
    path: 'your-tasks',
    loadComponent: () => import('@app/pages/your-tasks/your-tasks').then(m => m.YourTasks),
    canActivate: [authGuard],
  },
  {
    path: '',
    redirectTo: '/sign-in',
    pathMatch: 'full',
  },
];
