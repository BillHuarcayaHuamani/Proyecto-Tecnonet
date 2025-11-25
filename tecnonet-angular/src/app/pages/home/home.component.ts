import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PlanService } from '../../services/plan.service';
import { Plan } from '../../models/plan.model';
import { AuthService } from '../../services/auth.service';
import { ContratoService } from '../../services/contrato.service';
import { Contrato } from '../../models/contrato.model';

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
  contratoExistente: Contrato | null = null;
  
  showSuccessModal: boolean = false;
  showWarningModal: boolean = false;
  showContratoModal: boolean = false;
  
  modoEdicion: boolean = false;
  
  isSamePlanPending: boolean = false;

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
      distrito: ['', Validators.required],
      direccionExacta: ['', Validators.required],
      numeroTelefonoContacto: ['', Validators.required],
      metodoPago: ['Transferencia Bancaria', Validators.required],
      fechaInicioServicio: [''],
      observaciones: ['']
    });
  }

  ngOnInit(): void {
    this.planService.getPlanes().subscribe({
      next: (data) => this.planes = data.filter(plan => plan.activo),
      error: (err) => this.error = 'No se pudieron cargar los planes.'
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
        this.cargarContratoExistente();
      }
    });
  }

  cargarContratoExistente() {
      this.contratoService.getMiUltimoContrato().subscribe({
          next: (contrato) => this.contratoExistente = contrato,
          error: () => this.contratoExistente = null
      });
  }

  seleccionarPlan(plan: Plan): void {
    this.planSeleccionado = plan;
    this.isSamePlanPending = false;

    if (this.contratoExistente) {
        const estadoId = this.contratoExistente.estadoContrato.idEstadoContrato;
        const planIdActual = this.contratoExistente.plan.idPlan;
        
        if (estadoId === 2) {
            if (planIdActual === plan.idPlan) {
                this.isSamePlanPending = true;
                this.showWarningModal = true;
            } else {
                this.isSamePlanPending = false;
                this.showWarningModal = true;
            }
        } 
        else if (estadoId === 1) {
             if (planIdActual === plan.idPlan) {
                 alert("Ya tienes este plan activo actualmente.");
             } else {
                 this.iniciarEdicion();
             }
        }
        else {
            this.iniciarCreacion();
        }
    } else {
        this.iniciarCreacion();
    }
  }

  iniciarCreacion() {
      this.modoEdicion = false;
      this.contratoForm.patchValue({
          distrito: '', direccionExacta: '', numeroTelefonoContacto: '',
          fechaInicioServicio: '', observaciones: '',
          metodoPago: 'Transferencia Bancaria', costoInstalacion: 70.00
      });
      this.contratoForm.get('distrito')?.enable();
      this.contratoForm.get('direccionExacta')?.enable();
      
      this.showContratoModal = true;
  }

  iniciarEdicion() {
      this.modoEdicion = true;
      this.showWarningModal = false;
      
      if (!this.contratoExistente) return;

      const dirCompleta = this.contratoExistente.direccionInstalacion || '';
      const lastCommaIndex = dirCompleta.lastIndexOf(',');
      let direccionExacta = dirCompleta;
      let distrito = '';
      if (lastCommaIndex !== -1) {
          direccionExacta = dirCompleta.substring(0, lastCommaIndex).trim();
          distrito = dirCompleta.substring(lastCommaIndex + 1).trim();
      }

      this.contratoForm.patchValue({
          costoInstalacion: this.contratoExistente.costoInstalacion,
          distrito: distrito,
          direccionExacta: direccionExacta,
          numeroTelefonoContacto: this.contratoExistente.numeroTelefonoContacto,
          metodoPago: this.contratoExistente.metodoPago,
          fechaInicioServicio: this.contratoExistente.fechaInicioServicio,
          observaciones: this.contratoExistente.observaciones
      });
      
      if (this.contratoExistente.estadoContrato.idEstadoContrato === 1) {
          this.contratoForm.get('direccionExacta')?.disable();
          this.contratoForm.get('distrito')?.disable();
      } else {
          this.contratoForm.get('direccionExacta')?.enable();
          this.contratoForm.get('distrito')?.enable();
      }
      
      this.showContratoModal = true;
  }

  confirmarContratacion(): void {
    if (this.contratoForm.invalid || !this.planSeleccionado || !this.datosUsuario) {
      alert("Por favor, verifique los datos.");
      return;
    }
    
    const formData = this.contratoForm.getRawValue();
    const fechaInicio = formData.fechaInicioServicio || new Date().toISOString().split('T')[0];
    const direccionCompleta = `${formData.direccionExacta}, ${formData.distrito}`;

    const dataToSend = {
      idUsuario: this.datosUsuario.id,
      idPlan: this.planSeleccionado.idPlan,
      fechaContratacion: new Date().toISOString().split('T')[0],
      fechaInicioServicio: fechaInicio,
      fechaFinContrato: this.calcularFechaFin(fechaInicio),
      direccionInstalacion: direccionCompleta,
      numeroTelefonoContacto: formData.numeroTelefonoContacto,
      metodoPago: formData.metodoPago,
      costoInstalacion: formData.costoInstalacion,
      observaciones: formData.observaciones
    };

    if (this.modoEdicion && this.contratoExistente) {
        this.contratoService.actualizarDatosContrato(this.contratoExistente.idContrato, dataToSend).subscribe({
            next: (res) => {
                this.closeContratoModal();
                this.showSuccessModal = true;
                this.cargarContratoExistente(); 
            },
            error: (err) => alert("Error al actualizar.")
        });
    } else {
        this.contratoService.crearContrato(dataToSend).subscribe({
            next: (res) => {
                this.closeContratoModal();
                this.showSuccessModal = true;
                this.cargarContratoExistente();
            },
            error: (err) => alert("Error al crear contrato.")
        });
    }
  }

  closeSuccessModal(): void { this.showSuccessModal = false; this.planSeleccionado = null; }
  closeWarningModal(): void { this.showWarningModal = false; this.planSeleccionado = null; }
  closeContratoModal(): void { this.showContratoModal = false; }

  private calcularFechaFin(fechaInicio: string): string {
    const inicio = fechaInicio ? new Date(fechaInicio) : new Date();
    inicio.setFullYear(inicio.getFullYear() + 1);
    return inicio.toISOString().split('T')[0];
  }
}