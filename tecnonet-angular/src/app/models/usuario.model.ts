import { Rol } from './rol.model';

export interface Usuario {
  idUsuario: number;
  nombre: string;
  apellido: string;
  email: string;
  fechaRegistro: string;
  activo: boolean;
  rol: Rol;
}