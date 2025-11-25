export interface FacturaDTO {
  idFactura: number;
  idContrato: number;
  nombrePlan: string;
  nombreEstadoPago: string;
  montoTotal: number;
  fechaEmision: string;
  fechaVencimiento: string;
  metodoPago: string | null;
  fechaPago: string | null;
  descripcion: string;
}