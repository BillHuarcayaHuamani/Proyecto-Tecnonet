import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FacturaService } from '../../services/factura.service';
import { Factura } from '../../models/factura.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-invoice-list',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    DatePipe,
    FormsModule
  ],
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.css']
})
export class InvoiceListComponent implements OnInit {
  
  allFacturas: Factura[] = [];
  facturas: Factura[] = [];
  
  errorMessage: string | null = null;
  facturaSeleccionada: Factura | null = null;
  
  searchTerm: string = '';

  constructor(private facturaService: FacturaService) { }

  ngOnInit(): void {
    this.facturaService.getTodasFacturas().subscribe({
      next: (data) => {
        this.allFacturas = data;
        this.facturas = data;
      },
      error: (err) => {
        console.error("Error al cargar facturas:", err);
        this.errorMessage = "No se pudieron cargar las facturas.";
        this.allFacturas = [];
        this.facturas = [];
      }
    });
  }

  filterFacturas(): void {
    const term = this.searchTerm.toLowerCase().trim();

    if (!term) {
      this.facturas = [...this.allFacturas];
    } else {
      this.facturas = this.allFacturas.filter(factura => 
        (factura.contrato?.usuario?.nombre?.toLowerCase().includes(term) ||
         factura.contrato?.usuario?.apellido?.toLowerCase().includes(term)) ||
        factura.contrato?.idContrato?.toString().includes(term)
      );
    }
  }

  verDetalles(factura: Factura): void {
    this.facturaSeleccionada = factura;
  }

  getEstadoPagoClass(estado: string): string {
    if (!estado) return 'bg-secondary';
    switch (estado.toUpperCase()) {
      case 'PENDIENTE': return 'bg-warning text-dark';
      case 'PAGADA': return 'bg-success';
      case 'VENCIDA': return 'bg-danger';
      case 'ANULADA': return 'bg-secondary text-white';
      default: return 'bg-secondary';
    }
  }
}