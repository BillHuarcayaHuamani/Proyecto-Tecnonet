import { Injectable } from '@angular/core';
import { of, Observable } from 'rxjs';
import { Plan } from '../models/plan.model';

@Injectable({ providedIn: 'root' })
export class PlanService {
  private mockPlanes: Plan[] = [
    { idPlan: 1, nombrePlan: 'Plan Básico', descripcion: 'Ideal para navegar y redes sociales.', precioMensual: 50.00, velocidadDescargaMbps: 50, velocidadCargaMbps: 10, wifiIncluido: true, mesGratisPromocion: false, puertosEthernet: 2 },
    { idPlan: 2, nombrePlan: 'Plan Gamer', descripcion: 'Perfecto para streaming y juegos en línea.', precioMensual: 80.00, velocidadDescargaMbps: 200, velocidadCargaMbps: 50, wifiIncluido: true, mesGratisPromocion: true, puertosEthernet: 4 },
    { idPlan: 3, nombrePlan: 'Plan Premium', descripcion: 'Máxima velocidad para toda la familia.', precioMensual: 120.00, velocidadDescargaMbps: 500, velocidadCargaMbps: 100, wifiIncluido: true, mesGratisPromocion: true, puertosEthernet: 4 }
  ];

  getPlanes(): Observable<Plan[]> {
    return of(this.mockPlanes);
  }
}