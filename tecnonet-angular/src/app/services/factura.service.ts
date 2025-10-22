import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Factura } from '../models/factura.model';

@Injectable({
  providedIn: 'root'
})
export class FacturaService {
  private apiUrl = '/api/facturas';

  constructor(private http: HttpClient) { }

  getTodasFacturas(): Observable<Factura[]> {
    return this.http.get<Factura[]>(`${this.apiUrl}/todas`);
  }

  getMisFacturas(): Observable<Factura[]> {
    return this.http.get<Factura[]>(`${this.apiUrl}/mis-facturas`);
  }
}