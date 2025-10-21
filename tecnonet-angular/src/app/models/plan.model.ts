export interface Plan {
  idPlan: number;
  nombrePlan: string;
  velocidadDescargaMbps: number;
  velocidadCargaMbps: number;
  wifiIncluido: boolean;
  mesGratisPromocion: number;
  puertosEthernet: number;
  precioMensual: number;
  descripcion: string;
  fechaCreacion: string;
  activo: boolean;
}