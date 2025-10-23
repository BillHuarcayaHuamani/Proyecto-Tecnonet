import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ContactComponent } from './pages/contact/contact.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { MyInvoicesComponent } from './pages/my-invoices/my-invoices.component';
import { DashboardComponent } from './admin/dashboard/dashboard.component';
import { UserListComponent } from './admin/user-list/user-list.component';
import { ContractListComponent } from './admin/contract-list/contract-list.component';
import { MainLayoutComponent } from './shared/main-layout/main-layout.component';
import { ServiceListComponent } from './admin/service-list/service-list.component';
import { MessageListComponent } from './admin/message-list/message-list.component';
import { InvoiceListComponent } from './admin/invoice-list/invoice-list.component';
import { DashboardOperarioComponent } from './admin/dashboard-operario/dashboard-operario.component';
import { authGuard, adminGuard } from './services/auth.guard';
import { loginGuard } from './services/login.guard';

export const routes: Routes = [
  { path: 'iniciarSesion', component: LoginComponent, canActivate: [loginGuard] },
  { path: 'registrate', component: RegisterComponent, canActivate: [loginGuard] },

  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'home', component: HomeComponent },
      { path: 'contactanos', component: ContactComponent },
      { path: 'mis-facturas', component: MyInvoicesComponent },
      {
        path: 'admin',
        canActivate: [adminGuard],
        children: [
          { path: '', component: DashboardComponent },
          { path: 'dashboard-operario', component: DashboardOperarioComponent },
          { path: 'usuarios', component: UserListComponent },
          { path: 'contratos', component: ContractListComponent },
          { path: 'servicios', component: ServiceListComponent },
          { path: 'mensajes', component: MessageListComponent },
          { path: 'facturas', component: InvoiceListComponent }
        ]
      },
      { path: '', redirectTo: 'home', pathMatch: 'full' }
    ]
  },

  { path: '**', redirectTo: 'iniciarSesion' }
];