import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FacturaService } from '../../services/factura.service';
import { Factura } from '../../models/factura.model';

@Component({
  selector: 'app-my-invoices',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe],
  templateUrl: './my-invoices.component.html',
  styleUrls: ['./my-invoices.component.css']
})
export class MyInvoicesComponent implements OnInit {
  facturas: Factura[] = [];
  errorMessage: string | null = null;

  constructor(private facturaService: FacturaService) { }

  ngOnInit(): void {
    this.facturaService.getMisFacturas().subscribe({
      next: (data) => {
        this.facturas = data;
      },
      error: (err) => {
        console.error("Error al cargar mis facturas:", err);
        this.errorMessage = "No se pudieron cargar tus facturas.";
      }
    });
  }

  getEstadoPagoClass(estado: string): string {
    if (!estado) return 'bg-secondary';
    switch (estado.toUpperCase()) {
      case 'PENDIENTE': return 'bg-warning text-dark';
      case 'PAGADA': return 'bg-success';
      case 'VENCIDA': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }

  pagarFactura(facturaId: number): void {
    console.log("Simulando pago para factura ID:", facturaId);
    alert("Función de pago no implementada.");
  }
}