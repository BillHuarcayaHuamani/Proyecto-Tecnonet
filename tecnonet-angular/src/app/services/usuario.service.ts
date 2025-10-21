import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';

export interface LoginRequest {
  email: string;
  password?: string; 
}

export interface RegisterRequest {
  nombre: string;
  apellido: string;
  email: string;
  password?: string;
}

export interface AuthResponse {
  message: string;
  token?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private authApiUrl = '/api/auth';
  private usersApiUrl = '/api/usuarios';

  constructor(private http: HttpClient) { }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authApiUrl}/register`, userData);
  }

  getUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.usersApiUrl);
  }
}