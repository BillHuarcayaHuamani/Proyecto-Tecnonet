import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  
  adminEmail = "admin@tecnonet.com";

  showLogoutModal = false;

  constructor(private router: Router) {}

  confirmLogout(): void {
    console.log("Cerrando sesión desde el dashboard...");
    this.showLogoutModal = false;
    this.router.navigate(['/iniciarSesion']);
  }
}
