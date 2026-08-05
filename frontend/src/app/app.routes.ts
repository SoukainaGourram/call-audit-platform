import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { CallSearchComponent } from './components/call-search/call-search.component';
import { SearchHistoryComponent } from './components/search-history/search-history.component';
import { UserManagementComponent } from './components/user-management/user-management.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { authGuard, adminGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'search', component: CallSearchComponent, canActivate: [authGuard] },
  { path: 'history', component: SearchHistoryComponent, canActivate: [authGuard] },
  { path: 'users', component: UserManagementComponent, canActivate: [adminGuard] },
  { path: 'profile', component: UserProfileComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'search', pathMatch: 'full' },
  { path: '**', redirectTo: 'search' }
];
