import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Usuario } from '../../models/usuario.model';
import { UsuarioService } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  
  usuarios: Usuario[] = [];
  errorMessage: string | null = null;
  successMessage: string | null = null;
  esAdmin: boolean = false;

  nuevoPersonalForm: FormGroup;
  mostrarPassword = false;

  rolesCreacion = [
    { id: 2, nombre: 'Operario' },
    { id: 1, nombre: 'Administrador' }
  ];

  rolesDisponibles = [
    { id: 1, nombre: 'Administrador' },
    { id: 2, nombre: 'Operario' }
  ];

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private fb: FormBuilder
  ) { 
    this.nuevoPersonalForm = this.fb.group({
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      emailPrefix: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9.]+$')]], 
      password: ['', [Validators.required, Validators.minLength(6)]],
      idRol: [2, Validators.required]
    });
  }

  ngOnInit(): void {
    this.esAdmin = this.authService.hasRole('ADMINISTRADOR');
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.usuarioService.getUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = "No se pudieron cargar los usuarios.";
      }
    });
  }

  cambiarRol(usuario: Usuario, nuevoRolId: string): void {
    if (!this.esAdmin) return;

    const idRol = Number(nuevoRolId);
    if (usuario.rol.idRol === idRol) return;

    if(!confirm(`¿Estás seguro de cambiar el rol de ${usuario.nombre}?`)) {
        this.cargarUsuarios();
        return;
    }

    this.usuarioService.cambiarRol(usuario.idUsuario, idRol).subscribe({
      next: (usuarioActualizado) => {
        this.successMessage = `Rol actualizado correctamente para ${usuarioActualizado.email}`;
        this.errorMessage = null;
        
        const index = this.usuarios.findIndex(u => u.idUsuario === usuario.idUsuario);
        if (index !== -1) {
            this.usuarios[index] = usuarioActualizado;
        }
        
        setTimeout(() => this.successMessage = null, 3000);
      },
      error: (err) => {
        this.errorMessage = err.error?.error || "Error al actualizar.";
        this.cargarUsuarios();
      }
    });
  }

  toggleMostrarPassword(): void {
    this.mostrarPassword = !this.mostrarPassword;
  }

  crearPersonal(): void {
    if (this.nuevoPersonalForm.invalid) return;

    const formValue = this.nuevoPersonalForm.value;
    const emailCompleto = `${formValue.emailPrefix}@tecnonet.com`;
    const idRol = Number(formValue.idRol);

    const request: any = { 
      nombre: formValue.nombre,
      apellido: formValue.apellido,
      email: emailCompleto,
      password: formValue.password,
      idRol: idRol
    };

    this.usuarioService.crearPersonal(request).subscribe({
      next: (resp) => {
        const nombreRol = idRol === 1 ? 'Administrador' : 'Operario';
        this.successMessage = `Usuario de ${nombreRol} creado exitosamente.`;
        
        this.errorMessage = null;
        this.nuevoPersonalForm.reset({ idRol: 2 });
        this.mostrarPassword = false;
        this.cargarUsuarios();

        setTimeout(() => {
            this.successMessage = null;
        }, 3000);
      },
      error: (err) => {
        this.errorMessage = err.error?.error || "Error al crear el usuario.";
      }
    });
  }
}