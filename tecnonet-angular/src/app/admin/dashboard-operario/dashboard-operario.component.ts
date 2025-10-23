import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ContratoService } from '../../services/contrato.service';
import { SolicitudService } from '../../services/solicitud.service';
import { Contrato } from '../../models/contrato.model';
import { Solicitud } from '../../models/solicitud.model';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard-operario',
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    RouterLink
  ],
  templateUrl: './dashboard-operario.component.html',
  styleUrls: ['./dashboard-operario.component.css']
})
export class DashboardOperarioComponent implements OnInit {

  contratosPendientesCount: number = 0;
  solicitudesNuevasCount: number = 0;
  contratosActivadosHoyCount: number = 0;

  contratosPendientes: Contrato[] = [];
  solicitudesRecientes: Solicitud[] = [];

  errorMessage: string | null = null;

  constructor(
    private contratoService: ContratoService,
    private solicitudService: SolicitudService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.cargarContratosPendientes();
    this.cargarSolicitudesRecientes();
    this.cargarContratosActivadosHoy();
  }

  cargarContratosPendientes(): void {
    this.contratoService.getContratos().pipe(
      map(contratos => contratos.filter(c => c.estadoContrato.nombreEstado === 'Pendiente de Activación'))
    ).subscribe({
      next: (pendientes) => {
        this.contratosPendientes = pendientes.slice(0, 5);
        this.contratosPendientesCount = pendientes.length;
      },
      error: (err) => {
        console.error("Error al cargar contratos pendientes:", err);
        this.errorMessage = "Error al cargar contratos pendientes.";
      }
    });
  }

  cargarSolicitudesRecientes(): void {
    this.solicitudService.getSolicitudes().pipe(
      map(solicitudes => solicitudes.filter(s => !s.respuestasSolicitudes || s.respuestasSolicitudes.length === 0))
    ).subscribe({
      next: (nuevas) => {
        nuevas.sort((a, b) => new Date(b.fechaEnvio).getTime() - new Date(a.fechaEnvio).getTime());
        this.solicitudesRecientes = nuevas.slice(0, 5);
        this.solicitudesNuevasCount = nuevas.length;
      },
      error: (err) => {
        console.error("Error al cargar solicitudes:", err);
        this.errorMessage = "Error al cargar solicitudes recientes.";
      }
    });
  }

  cargarContratosActivadosHoy(): void {
  const hoy = new Date().toISOString().split('T')[0]; // Formato YYYY-MM-DD
  this.contratoService.getContratos().pipe(
    map(contratos => contratos.filter(c =>
      c.estadoContrato.nombreEstado === 'Activo' &&
      c.fechaActivacion?.startsWith(hoy) // <-- Usa el nuevo campo
    ))
  ).subscribe({
    next: (activados) => {
      this.contratosActivadosHoyCount = activados.length;
    },
    error: (err) => console.error("Error al cargar contratos activados hoy:", err)
  });
}

  irAContrato(id: number): void {
    this.router.navigate(['/admin/contratos'], { queryParams: { focus: id } });
  }

  irASolicitud(id: number): void {
    this.router.navigate(['/admin/mensajes'], { queryParams: { focus: id } });
  }
}