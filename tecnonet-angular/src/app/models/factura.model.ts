import { EstadoPago } from './estado-pago.model';
import { Contrato } from './contrato.model'; 

export interface Factura {
  idFactura: number;
  contrato: Contrato;
  estadoPago: EstadoPago;
  montoTotal: number;
  fechaEmision: string;
  fechaVencimiento: string;
  metodoPago: string;
  fechaPago: string | null;
  descripcion: string;
}