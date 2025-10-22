import { Usuario } from "./usuario.model";

export interface RespuestaSolicitud {
  idRespuesta: number;
  operario: Usuario; 
  respuesta: string;
  fechaRespuesta: string;
}
