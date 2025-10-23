import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Factura } from '../models/factura.model';
import { FacturaDTO } from '../models/factura.dto'; 

export interface PagoRequest {
  metodoPago: string;
}

@Injectable({
  providedIn: 'root'
})
export class FacturaService {
  private apiUrl = '/api/facturas';

  constructor(private http: HttpClient) { }

  getTodasFacturas(): Observable<Factura[]> {
    return this.http.get<Factura[]>(`${this.apiUrl}/todas`);
  }

  getMisFacturas(): Observable<FacturaDTO[]> {
    return this.http.get<FacturaDTO[]>(`${this.apiUrl}/mis-facturas`);
  }

  marcarComoPagada(idFactura: number, metodoPago: string): Observable<FacturaDTO> {
    const url = `${this.apiUrl}/${idFactura}/pagar`; 
    const body: PagoRequest = { metodoPago };  
    return this.http.put<FacturaDTO>(url, body);
  }
}