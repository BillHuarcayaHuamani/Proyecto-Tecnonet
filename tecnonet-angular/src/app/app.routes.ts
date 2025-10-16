import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ContactComponent } from './pages/contact/contact.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './admin/dashboard/dashboard.component';
import { UserListComponent } from './admin/user-list/user-list.component';
import { ContractListComponent } from './admin/contract-list/contract-list.component';
import { MainLayoutComponent } from './shared/main-layout/main-layout.component';

export const routes: Routes = [
  { path: '', redirectTo: '/iniciarSesion', pathMatch: 'full' },

  { path: 'iniciarSesion', component: LoginComponent },
  { path: 'registrate', component: RegisterComponent },

  {
    path: '', 
    component: MainLayoutComponent,
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'contactanos', component: ContactComponent },
      { path: 'admin', component: DashboardComponent },
      { path: 'admin/usuarios', component: UserListComponent },
      { path: 'admin/contratos', component: ContractListComponent }
    ]
  },

  { path: '**', redirectTo: '/iniciarSesion' }
];