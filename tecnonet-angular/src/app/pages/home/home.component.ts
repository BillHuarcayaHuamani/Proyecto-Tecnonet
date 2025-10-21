import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PlanService } from '../../services/plan.service';
import { Plan } from '../../models/plan.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  planes: Plan[] = [];
  error: string | null = null;
  
  planSeleccionado: Plan | null = null;
  contratoForm: FormGroup;
  
  datosUsuario: any = null;

  constructor(
    private planService: PlanService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.contratoForm = this.fb.group({
      nombreCliente: [{ value: '', disabled: true }],
      apellidoCliente: [{ value: '', disabled: true }],
      emailCliente: [{ value: '', disabled: true }],
      costoInstalacion: [70.00, Validators.required],
      direccionInstalacion: ['', Validators.required],
      numeroTelefonoContacto: ['', Validators.required],
      metodoPago: ['Transferencia Bancaria', Validators.required],
      fechaInicioServicio: [''],
      observaciones: ['']
    });
  }

  ngOnInit(): void {
    this.planService.getPlanes().subscribe({
      next: (data) => {
        this.planes = data.filter(plan => plan.activo);
      },
      error: (err) => {
        console.error('Error al cargar planes:', err);
        this.error = 'No se pudieron cargar los planes. Intente más tarde.';
      }
    });

    this.authService.currentUser.subscribe(user => {
      if (user) {
        this.datosUsuario = {
          id: user.id,
          nombre: user.nombre,
          apellido: '', 
          email: user.sub 
        };
        
        this.contratoForm.patchValue({
          nombreCliente: this.datosUsuario.nombre,
          apellidoCliente: this.datosUsuario.apellido,
          emailCliente: this.datosUsuario.email
        });
      }
    });
  }

  seleccionarPlan(plan: Plan): void {
    this.planSeleccionado = plan;
  }

  confirmarContratacion(): void {
    if (this.contratoForm.invalid || !this.planSeleccionado) {
      alert("Por favor, complete todos los campos requeridos.");
      return;
    }
    
    const formData = this.contratoForm.getRawValue();
    
    const nuevoContrato = {
      idUsuario: this.datosUsuario.id,
      idPlan: this.planSeleccionado.idPlan,
      idEstadoContrato: 1,
      fechaContratacion: new Date().toISOString().split('T')[0],
      fechaInicioServicio: formData.fechaInicioServicio || new Date().toISOString().split('T')[0],
      fechaFinContrato: this.calcularFechaFin(formData.fechaInicioServicio),
      direccionInstalacion: formData.direccionInstalacion,
      numeroTelefonoContacto: formData.numeroTelefonoContacto,
      metodoPago: formData.metodoPago,
      costoInstalacion: formData.costoInstalacion,
      observaciones: formData.observaciones
    };

    console.log("Enviando nuevo contrato (simulado):", nuevoContrato);
  }

  private calcularFechaFin(fechaInicio: string): string {
    const inicio = fechaInicio ? new Date(fechaInicio) : new Date();
    inicio.setFullYear(inicio.getFullYear() + 1);
    return inicio.toISOString().split('T')[0];
  }
}