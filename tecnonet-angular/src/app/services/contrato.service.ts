import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Contrato } from '../models/contrato.model';
import { EstadoContrato } from '../models/estado-contrato.model';

@Injectable({
  providedIn: 'root'
})
export class ContratoService {
  private apiUrl = '/api/contratos';
  private apiUrlEstados = '/api/estados-contrato';

  constructor(private http: HttpClient) { }

  getContratos(): Observable<Contrato[]> {
    return this.http.get<Contrato[]>(this.apiUrl);
  }

  getEstadoContratos(): Observable<EstadoContrato[]> {
    return this.http.get<EstadoContrato[]>(this.apiUrlEstados);
  }

  actualizarEstado(contratoId: number, nuevoEstadoId: number): Observable<Contrato> {
    const url = `${this.apiUrl}/${contratoId}/estado`;
    return this.http.put<Contrato>(url, nuevoEstadoId, {
      headers: { 'Content-Type': 'application/json' }
    });
  }
}