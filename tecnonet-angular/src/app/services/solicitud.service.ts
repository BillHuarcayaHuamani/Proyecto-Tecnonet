import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Solicitud } from '../models/solicitud.model';

export interface SolicitudRequest {
  asunto: string;
  mensaje: string;
  nombreRemitente?: string;
  apellidoRemitente?: string;
  correoRemitente?: string;
  telefonoRemitente?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SolicitudService {
  private apiUrl = '/api/solicitudes';

  constructor(private http: HttpClient) { }

  getSolicitudes(): Observable<Solicitud[]> {
    return this.http.get<Solicitud[]>(this.apiUrl);
  }

  enviarSolicitud(solicitudData: SolicitudRequest): Observable<any> {
    return this.http.post<any>(this.apiUrl, solicitudData);
  }
}