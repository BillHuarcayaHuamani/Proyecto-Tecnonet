import { Usuario } from './usuario.model';
import { Plan } from './plan.model';

export interface Contrato {
  idContrato: number;
  usuario: Usuario;
  plan: Plan;
  fechaContratacion: string;
  estadoContrato: 'ACTIVO' | 'PENDIENTE_INSTALACION' | 'CANCELADO' | 'FINALIZADO';
  direccionInstalacion: string;
  numeroTelefonoContacto: string;
  metodoPago: string;
  costoInstalacion: number;
  fechaInicioServicio: string;
  fechaFinContrato: string;
  observaciones: string;
}