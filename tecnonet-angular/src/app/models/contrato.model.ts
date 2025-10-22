import { Usuario } from './usuario.model';
import { Plan } from './plan.model';
import { EstadoContrato } from './estado-contrato.model';

export interface Contrato {
  idContrato: number;
  usuario: Usuario;
  plan: Plan;
  estadoContrato: EstadoContrato;
  fechaContratacion: string;
  fechaInicioServicio: string;
  fechaFinContrato: string;
  direccionInstalacion: string;
  numeroTelefonoContacto: string;
  metodoPago: string;
  costoInstalacion: number;
  observaciones: string;
}