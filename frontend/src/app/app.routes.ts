import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'search',
    loadComponent: () => import('./pages/search-results/search-results.component').then(m => m.SearchResultsComponent)
  },
  {
    path: 'streets/:id',
    loadComponent: () => import('./pages/street-detail/street-detail.component').then(m => m.StreetDetailComponent)
  },
  {
    path: 'streets/:id/review',
    loadComponent: () => import('./pages/review-form/review-form.component').then(m => m.ReviewFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'verify-email',
    loadComponent: () => import('./pages/verify-email/verify-email.component').then(m => m.VerifyEmailComponent)
  },
  {
    path: 'oauth2/callback',
    loadComponent: () => import('./pages/oauth-callback/oauth-callback.component').then(m => m.OAuthCallbackComponent)
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [authGuard]
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent)
      },
      {
        path: 'users',
        loadComponent: () => import('./pages/admin/admin-users/admin-users.component').then(m => m.AdminUsersComponent)
      },
      {
        path: 'reviews',
        loadComponent: () => import('./pages/admin/admin-reviews/admin-reviews.component').then(m => m.AdminReviewsComponent)
      },
      {
        path: 'mails',
        loadComponent: () => import('./pages/admin/admin-mails/admin-mails.component').then(m => m.AdminMailsComponent)
      },
      {
        path: 'audit',
        loadComponent: () => import('./pages/admin/admin-audit/admin-audit.component').then(m => m.AdminAuditComponent)
      },
      {
        path: 'scheduled-actions',
        loadComponent: () => import('./pages/admin/admin-scheduled-actions/admin-scheduled-actions.component').then(m => m.AdminScheduledActionsComponent)
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
