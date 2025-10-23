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
      },
      error: (err) => {
        console.error("Error al cargar estados:", err);
      }
    });
  }
  
  getOpcionesDeEstado(contrato: Contrato): EstadoContrato[] {
    const estadoActual = contrato.estadoContrato;
    
    if (!estadoActual || this.estadosContrato.length === 0) {
      return [];
    }

    let opciones: EstadoContrato[] = [estadoActual];

    switch (estadoActual.nombreEstado) {
      case 'Pendiente de Activación':
        opciones.push(...this.estadosContrato.filter(e => 
          e.idEstadoContrato === 1 || e.idEstadoContrato === 4
        ));
        break;
      case 'Activo':
        opciones.push(...this.estadosContrato.filter(e => 
          e.idEstadoContrato === 3 || e.idEstadoContrato === 4
        ));
        break;
    }
    
    return opciones;
  }
  
  
  onEstadoChange(contrato: Contrato, event: Event): void {
    this.errorMessage = null;
    this.successMessage = null;

    const selectElement = event.target as HTMLSelectElement;
    const nuevoEstadoId = Number(selectElement.value);
    const estadoOriginalId = contrato.estadoContrato.idEstadoContrato;

    if (nuevoEstadoId === estadoOriginalId) return;

    this.contratoService.actualizarEstado(contrato.idContrato, nuevoEstadoId).subscribe({
      next: (contratoActualizado) => {
        const estadoEncontrado = this.estadosContrato.find(e => e.idEstadoContrato === nuevoEstadoId);
        if (estadoEncontrado) {
             contrato.estadoContrato = estadoEncontrado;
        }
        
        this.mostrarMensajeExito("Estado actualizado con éxito.");
        if(nuevoEstadoId === 1 && estadoOriginalId === 2) { 
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
    switch (estadoNombre) {
      case 'Activo': return 'bg-success text-white';
      case 'Pendiente de Activación': return 'bg-warning text-dark';
      case 'Cancelado': return 'bg-danger text-white';
      case 'Finalizado': return 'bg-info text-dark';
      default: return 'bg-secondary text-white';
    }
  }

  getSelectBackgroundClass(estadoNombre: string): string {
      if (!estadoNombre) return '';
      switch (estadoNombre) {
        case 'Activo': return 'bg-success text-white';
        case 'Pendiente de Activación': return 'bg-warning text-dark';
        case 'Cancelado': return 'bg-danger text-white';
        case 'Finalizado': return 'bg-info text-dark';
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