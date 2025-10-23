import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PlanService } from '../../services/plan.service';
import { Plan } from '../../models/plan.model';
import { AuthService } from '../../services/auth.service';
import { ContratoService } from '../../services/contrato.service';
import { Router } from '@angular/router';

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

  showSuccessModal: boolean = false;

  constructor(
    private planService: PlanService,
    private authService: AuthService,
    private fb: FormBuilder,
    private contratoService: ContratoService,
    private router: Router
  ) {
    this.contratoForm = this.fb.group({
      nombreCliente: [{ value: '', disabled: true }],
      apellidoCliente: [{ value: '', disabled: true }],
      emailCliente: [{ value: '', disabled: true }],
      costoInstalacion: [{ value: 70.00, disabled: true }, Validators.required],
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
          apellido: user.apellido || '',
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
    this.contratoForm.reset({
        nombreCliente: this.datosUsuario?.nombre,
        apellidoCliente: this.datosUsuario?.apellido,
        emailCliente: this.datosUsuario?.email,
        costoInstalacion: 70.00,
        metodoPago: 'Transferencia Bancaria',
        fechaInicioServicio: '',
        observaciones: ''
      });
  }

  confirmarContratacion(): void {
    if (this.contratoForm.invalid || !this.planSeleccionado) {
      alert("Por favor, complete todos los campos requeridos.");
      return;
    }
    if (!this.datosUsuario) {
      alert("Error: No se han podido cargar los datos del usuario. Por favor, inicie sesión de nuevo.");
      return;
    }

    const formData = this.contratoForm.getRawValue();
    const fechaInicio = formData.fechaInicioServicio || new Date().toISOString().split('T')[0];

    const nuevoContrato = {
      idUsuario: this.datosUsuario.id,
      idPlan: this.planSeleccionado.idPlan,
      fechaContratacion: new Date().toISOString().split('T')[0],
      fechaInicioServicio: fechaInicio,
      fechaFinContrato: this.calcularFechaFin(fechaInicio),
      direccionInstalacion: formData.direccionInstalacion,
      numeroTelefonoContacto: formData.numeroTelefonoContacto,
      metodoPago: formData.metodoPago,
      costoInstalacion: formData.costoInstalacion,
      observaciones: formData.observaciones
    };

    this.contratoService.crearContrato(nuevoContrato).subscribe({
      next: (contratoCreado) => {
        console.log("Contrato creado exitosamente:", contratoCreado);
        this.showSuccessModal = true;
         this.contratoForm.reset({
            nombreCliente: this.datosUsuario?.nombre,
            apellidoCliente: this.datosUsuario?.apellido,
            emailCliente: this.datosUsuario?.email,
            costoInstalacion: 70.00,
            metodoPago: 'Transferencia Bancaria',
            fechaInicioServicio: '',
            observaciones: ''
          });
          this.planSeleccionado = null;
      },
      error: (err) => {
        console.error("Error al crear el contrato:", err);
        alert("Hubo un error al procesar su solicitud. Por favor, intente más tarde.");
      }
    });
  }

  closeSuccessModal(): void {
    this.showSuccessModal = false;
  }

  private calcularFechaFin(fechaInicio: string): string {
    const inicio = fechaInicio ? new Date(fechaInicio) : new Date();
    inicio.setFullYear(inicio.getFullYear() + 1);
    return inicio.toISOString().split('T')[0];
  }
}