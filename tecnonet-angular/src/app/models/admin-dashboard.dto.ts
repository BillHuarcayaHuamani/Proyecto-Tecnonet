import { Contrato } from "./contrato.model";
import { Usuario } from "./usuario.model";

export interface AdminDashboardDTO {

    ingresosDelMes: number;
    totalClientesActivos: number;
    totalContratosActivos: number;
    solicitudesPendientes: number;

    nuevosClientes: Usuario[];
    contratosPendientes: Contrato[];
}