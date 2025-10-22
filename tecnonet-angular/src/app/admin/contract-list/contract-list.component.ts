import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { ContratoService } from '../../services/contrato.service';
import { Contrato } from '../../models/contrato.model';
import { EstadoContrato } from '../../models/estado-contrato.model'; 
import { FormsModule } from '@angular/forms'; 
import { ReplacePipe } from '../../shared/pipes/replace.pipe';

@Component({
  selector: 'app-contract-list',
  standalone: true,
  imports: [CommonModule, DatePipe, CurrencyPipe, FormsModule, ReplacePipe],
  templateUrl: './contract-list.component.html',
  styleUrls: ['./contract-list.component.css']
})
export class ContractListComponent implements OnInit {
  
  contratos: Contrato[] = [];
  estadosContrato: EstadoContrato[] = []; 
  
  estadosDisponibles: EstadoContrato[] = []; 

  errorMessage: string | null = null;
  successMessage: string | null = null;
  contratoSeleccionado: Contrato | null = null;

  constructor(private contratoService: ContratoService) { }

  ngOnInit(): void {
    this.cargarContratos();
    this.cargarEstados(); 
  }

  cargarContratos(): void {
    this.contratoService.getContratos().subscribe({
      next: (data) => {
        this.contratos = data;
      },
      error: (err) => {
        console.error("Error al cargar contratos:", err);
        this.errorMessage = "No se pudieron cargar los contratos.";
      }
    });
  }

  cargarEstados(): void {
    this.contratoService.getEstadoContratos().subscribe({
      next: (data) => {
        this.estadosContrato = data; 
        
        this.estadosDisponibles = data.filter(estado => 
          estado.idEstadoContrato === 1 || estado.idEstadoContrato === 2
        );
      },
      error: (err) => {
        console.error("Error al cargar estados:", err);
      }
    });
  }

  onEstadoChange(contrato: Contrato, event: Event): void {
    this.errorMessage = null;
    this.successMessage = null;

    const selectElement = event.target as HTMLSelectElement;
    const nuevoEstadoId = Number(selectElement.value);
    const estadoOriginalId = contrato.estadoContrato.idEstadoContrato;

    this.contratoService.actualizarEstado(contrato.idContrato, nuevoEstadoId).subscribe({
      next: (contratoActualizado) => {
        const estadoEncontrado = this.estadosContrato.find(e => e.idEstadoContrato === nuevoEstadoId);
        if (estadoEncontrado) {
             contrato.estadoContrato = estadoEncontrado;
        }
        
        this.mostrarMensajeExito("Estado actualizado con éxito.");
        if(nuevoEstadoId === 1) { 
            this.mostrarMensajeExito("Estado actualizado. Generando facturas...", 5000);
        }
      },
      error: (err) => {
        console.error("Error al actualizar estado:", err);
        this.errorMessage = "Error al actualizar el estado.";
        selectElement.value = String(estadoOriginalId);
      }
    });
  }
  
  verDetalles(contrato: Contrato): void {
    this.contratoSeleccionado = contrato;
  }
  
  getEstadoClass(estadoNombre: string): string {
    if (!estadoNombre) return 'bg-secondary';
    switch (estadoNombre.toUpperCase()) {
      case 'ACTIVO': return 'bg-success text-white';
      case 'PENDIENTE DE INSTALACIÓN': return 'bg-warning text-dark';
      case 'CANCELADO': return 'bg-danger text-white';
      case 'FINALIZADO': return 'bg-info text-dark';
      default: return 'bg-secondary text-white';
    }
  }

  getSelectBackgroundClass(estadoNombre: string): string {
      if (!estadoNombre) return '';
      switch (estadoNombre.toUpperCase()) {
        case 'ACTIVO': return 'bg-success text-white';
        case 'PENDIENTE DE INSTALACIÓN': return 'bg-warning text-dark';
        case 'CANCELADO': return 'bg-danger text-white';
        case 'FINALIZADO': return 'bg-info text-dark';
        default: return 'bg-secondary text-white';
      }
  }

  mostrarMensajeExito(mensaje: string, duracion: number = 3000): void {
    this.successMessage = mensaje;
    setTimeout(() => {
      this.successMessage = null;
    }, duracion);
  }
}