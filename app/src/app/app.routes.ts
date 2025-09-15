import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'sign-in',
    loadComponent: () => import('./layouts/sign-in-layout/sign-in-layout.component').then(m => m.SignInLayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/sign-in/sign-in.component').then(m => m.SignInComponent)
      }
    ]
  },
  {
    path: 'sign-up',
    loadComponent: () => import('./layouts/sign-in-layout/sign-in-layout.component').then(m => m.SignInLayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/sign-up/sign-up.component').then(m => m.SignUpComponent)
      }
    ]
  },
  {
    path: '',
    redirectTo: '/sign-in',
    pathMatch: 'full'
  }
];
