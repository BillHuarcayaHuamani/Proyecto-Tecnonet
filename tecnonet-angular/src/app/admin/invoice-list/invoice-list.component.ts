import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FacturaService } from '../../services/factura.service';
import { Factura } from '../../models/factura.model';

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [CommonModule, CurrencyPipe, DatePipe],
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.css']
})
export class InvoiceListComponent implements OnInit {
  facturas: Factura[] = [];
  errorMessage: string | null = null;

  constructor(private facturaService: FacturaService) { }

  ngOnInit(): void {
    this.facturaService.getTodasFacturas().subscribe({
      next: (data) => {
        this.facturas = data;
      },
      error: (err) => {
        console.error("Error al cargar facturas:", err);
        this.errorMessage = "No se pudieron cargar las facturas.";
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
}