export interface Plan {
  idPlan: number;
  nombrePlan: string;
  descripcion: string;
  precioMensual: number;
  velocidadDescargaMbps: number;
  velocidadCargaMbps: number;
  wifiIncluido: boolean;
  mesGratisPromocion: boolean;
  puertosEthernet: number;
}