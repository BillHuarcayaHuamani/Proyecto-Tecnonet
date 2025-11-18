import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common'; 
import { Router, RouterLink } from '@angular/router';
import { DashboardService } from '../../services/dashboard.service';
import { AdminDashboardDTO } from '../../models/admin-dashboard.dto';

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe],
  templateUrl: './dashboard-admin.component.html',
  styleUrls: ['./dashboard-admin.component.css']
})
export class DashboardAdminComponent implements OnInit {

  data: AdminDashboardDTO | null = null;
  isLoading: boolean = true;
  errorMessage: string | null = null;

  constructor(
    private dashboardService: DashboardService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.cargarDatosDashboard();
  }

  cargarDatosDashboard(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.dashboardService.getAdminDashboardData().subscribe({
      next: (data) => {
        this.data = data;
        this.isLoading = false;
        console.log("Datos del Dashboard Admin cargados:", data);
      },
      error: (err) => {
        console.error("Error al cargar datos del dashboard:", err);
        this.errorMessage = "No se pudieron cargar los datos del dashboard. " + (err.error?.error || err.message);
        this.isLoading = false;
      }
    });
  }

  irAContratos(id?: number): void {
    const params = id ? { queryParams: { focus: id } } : {};
    this.router.navigate(['/admin/contratos'], params);
  }

  irAUsuarios(id?: number): void {
    const params = id ? { queryParams: { focus: id } } : {};
    this.router.navigate(['/admin/usuarios'], params);
  }

  irASolicitudes(): void {
    this.router.navigate(['/admin/mensajes']);
  }
}