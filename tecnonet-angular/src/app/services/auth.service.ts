import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, AuthResponse } from './usuario.service';

export interface DecodedToken {
  sub: string;
  id: number;
  nombre: string;
  apellido: string;
  rol: string;
  iat: number;
  exp: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private tokenKey = 'tecnonet_token';

  private currentUserSubject: BehaviorSubject<DecodedToken | null>;
  public currentUser: Observable<DecodedToken | null>;

  constructor(private http: HttpClient) {
    let user: DecodedToken | null = null;
    try {
      const token = this.getToken();
      user = token ? this.decodeToken(token) : null;
      if (user && user.exp * 1000 < Date.now()) {
        user = null;
        localStorage.removeItem(this.tokenKey);
      }
    } catch (error) {
      localStorage.removeItem(this.tokenKey);
    }
    this.currentUserSubject = new BehaviorSubject<DecodedToken | null>(user);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): DecodedToken | null {
    return this.currentUserSubject.value;
  }

  public getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  public isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    try {
        const user = this.decodeToken(token);
        if (user && user.exp * 1000 > Date.now()) {
          return true;
        }
    } catch(e) {}
    localStorage.removeItem(this.tokenKey);
    this.currentUserSubject.next(null);
    return false;
  }

  public isStaff(): boolean {
      const user = this.currentUserValue;
      if (!user) return false;
      const userRol = user.rol.toUpperCase();
      return userRol === 'ADMINISTRADOR' || userRol === 'OPERARIO';
  }

   public hasRole(roleName: string): boolean {
       const user = this.currentUserValue;
       return !!user && user.rol.toUpperCase() === roleName.toUpperCase();
   }

  public isAdmin(): boolean {
      const user = this.currentUserValue;
      if (!user) {
          return false;
      }
      const userRol = user.rol.toUpperCase();
      return userRol === 'ADMINISTRADOR' || userRol === 'OPERARIO';
  }


  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response && response.token) {
          localStorage.setItem(this.tokenKey, response.token);
          const decodedUser = this.decodeToken(response.token);
          this.currentUserSubject.next(decodedUser);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.currentUserSubject.next(null);
  }

  public decodeToken(token: string): DecodedToken | null {
    try {
      const payload = token.split('.')[1];
      const decodedPayload = atob(payload);
      return JSON.parse(decodedPayload) as DecodedToken;
    } catch (error) {
      console.error("Error decodificando el token", error);
      throw new Error("Invalid token");
    }
  }
}