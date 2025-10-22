import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RespuestaPayload {
  respuesta: string;
}

@Injectable({
  providedIn: 'root'
})
export class RespuestaService {

  constructor(private http: HttpClient) { }

  guardarRespuesta(solicitudId: number, payload: RespuestaPayload): Observable<any> {
    const url = `/api/solicitudes/${solicitudId}/respuestas`;
    return this.http.post<any>(url, payload);
  }
}