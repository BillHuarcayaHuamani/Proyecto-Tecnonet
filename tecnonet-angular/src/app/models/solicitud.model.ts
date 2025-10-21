import { Usuario } from "./usuario.model";
import { RespuestaSolicitud } from "./respuesta-solicitud.model"; 

export interface Solicitud {
  idSolicitud: number;
  usuario: Usuario;
  asunto: string;
  mensaje: string;
  numeroRemitente: string;
  fechaEnvio: string;
  apellidoRemitente: string;
  telefonoRemitente: string;
  respuestasSolicitudes: RespuestaSolicitud[];
}