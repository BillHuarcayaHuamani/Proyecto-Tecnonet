export interface FacturaDTO {
  idFactura: number;
  idContrato: number; 
  nombreEstadoPago: string; 
  montoTotal: number;
  fechaEmision: string; 
  fechaVencimiento: string; 
  metodoPago: string | null;
  fechaPago: string | null; 
  descripcion: string;
}