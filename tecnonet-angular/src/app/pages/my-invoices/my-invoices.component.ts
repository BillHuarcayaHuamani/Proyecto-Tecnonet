import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FacturaService, PagoRequest } from '../../services/factura.service';
import { FacturaDTO } from '../../models/factura.dto'; 
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-my-invoices',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    DatePipe,
    FormsModule
  ],
  templateUrl: './my-invoices.component.html',
  styleUrls: ['./my-invoices.component.css']
})
export class MyInvoicesComponent implements OnInit {
  facturas: FacturaDTO[] = []; 
  errorMessage: string | null = null;
  showPaymentModal: boolean = false;
  facturaAPagar: FacturaDTO | null = null; 

  metodoPagoSeleccionado: string = 'Tarjeta de Crédito';
  paymentErrorMessage: string | null = null;

  constructor(private facturaService: FacturaService) { }

  ngOnInit(): void {
    this.cargarMisFacturas();
  }

  cargarMisFacturas(): void {
    this.errorMessage = null;
    this.facturaService.getMisFacturas().subscribe({ 
      next: (data) => {
        this.facturas = data.sort((a, b) => {
          const dateA = new Date(a.fechaEmision).getTime();
          const dateB = new Date(b.fechaEmision).getTime();
          return dateA - dateB; 
        });
      },
      error: (err) => {
        console.error("Error al cargar mis facturas:", err);
        this.errorMessage = "No se pudieron cargar tus facturas.";
        this.facturas = [];
      }
    });
  }

  getEstadoPagoClass(estadoNombre: string): string { 
    if (!estadoNombre) return 'bg-secondary';
    switch (estadoNombre.toUpperCase()) { 
      case 'PENDIENTE': return 'bg-warning text-dark';
      case 'PAGADA': return 'bg-success text-white';
      case 'VENCIDA': return 'bg-danger text-white';
      case 'ANULADA': return 'bg-secondary text-white';
      default: return 'bg-secondary text-white';
    }
  }

  pagarFactura(factura: FacturaDTO): void {
    this.facturaAPagar = factura; 
    this.metodoPagoSeleccionado = 'Tarjeta de Crédito';
    this.paymentErrorMessage = null;
    this.showPaymentModal = true;
  }

  closePaymentModal(): void {
    this.showPaymentModal = false;
    this.facturaAPagar = null;
    this.paymentErrorMessage = null;
  }

  confirmarPago(): void {
    if (!this.facturaAPagar || !this.metodoPagoSeleccionado) {
      this.paymentErrorMessage = "Error inesperado.";
      return;
    }
    this.paymentErrorMessage = null;
    const facturaId = this.facturaAPagar.idFactura;
    const metodoPago = this.metodoPagoSeleccionado;

    this.facturaService.marcarComoPagada(facturaId, metodoPago).subscribe({ 
      next: (facturaActualizadaDTO) => {
        this.cargarMisFacturas(); 
        this.closePaymentModal();
      },
      error: (err) => {
        console.error("Error al marcar como pagada:", err);
        if (err.status === 400 && err.error?.error) { 
          this.paymentErrorMessage = err.error.error;
        } else if (err.status === 403 && err.error?.error) {
            this.paymentErrorMessage = err.error.error;
        } else if (err.status === 404 && err.error?.error){
            this.paymentErrorMessage = err.error.error;
        }
        else {
          this.paymentErrorMessage = "Error al procesar el pago. Inténtalo más tarde.";
        }
      }
    });
  }
}