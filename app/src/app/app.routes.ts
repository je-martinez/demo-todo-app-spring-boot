import { Routes } from '@angular/router';
import { ROUTE_METADATA } from './config/route-metadata.config';

export const routes: Routes = [
  {
    path: 'sign-in',
    loadComponent: () =>
      import('@layouts/sign-in-layout/sign-in-layout.component').then(m => m.SignInLayoutComponent),
    title: ROUTE_METADATA.SIGN_IN.title,
    data: {
      meta: ROUTE_METADATA.SIGN_IN,
    },
    children: [
      {
        path: '',
        loadComponent: () =>
          import('@pages/sign-in/sign-in.component').then(m => m.SignInComponent),
      },
    ],
  },
  {
    path: 'sign-up',
    loadComponent: () =>
      import('@layouts/sign-in-layout/sign-in-layout.component').then(m => m.SignInLayoutComponent),
    title: ROUTE_METADATA.SIGN_UP.title,
    data: {
      meta: ROUTE_METADATA.SIGN_UP,
    },
    children: [
      {
        path: '',
        loadComponent: () =>
          import('@pages/sign-up/sign-up.component').then(m => m.SignUpComponent),
      },
    ],
  },
  {
    path: '',
    redirectTo: '/sign-in',
    pathMatch: 'full',
  },
];
