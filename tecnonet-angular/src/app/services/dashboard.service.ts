import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AdminDashboardDTO } from '../models/admin-dashboard.dto';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private apiUrl = '/api/dashboard';

  constructor(private http: HttpClient) { }

  getAdminDashboardData(): Observable<AdminDashboardDTO> {
    return this.http.get<AdminDashboardDTO>(`${this.apiUrl}/admin`);
  }
}