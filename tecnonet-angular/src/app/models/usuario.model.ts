export interface Usuario {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  tipoUsuario: string;
  activo: boolean;
  fechaRegistro: string;
}