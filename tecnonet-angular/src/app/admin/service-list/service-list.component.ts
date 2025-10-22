import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Plan } from '../../models/plan.model';
import { PlanService } from '../../services/plan.service';

@Component({
  selector: 'app-service-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CurrencyPipe, DatePipe],
  templateUrl: './service-list.component.html',
  styleUrls: ['./service-list.component.css']
})
export class ServiceListComponent implements OnInit {

  planes: Plan[] = [];
  nuevoPlanForm: FormGroup;
  modificarPlanForm: FormGroup;
  planSeleccionado: Plan | null = null;
  mensajeExito: string | null = null;
  errorMessage: string | null = null;

  @ViewChild('modificarPlanModal') modificarModalElement!: ElementRef;

  constructor(private fb: FormBuilder, private planService: PlanService) {
    this.nuevoPlanForm = this.fb.group({
      nombrePlan: ['', Validators.required],
      velocidadDescargaMbps: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
      velocidadCargaMbps: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
      wifiIncluido: ['true', Validators.required],
      mesGratisPromocion: [0, [Validators.required]],
      puertosEthernet: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
      precioMensual: ['', [Validators.required, Validators.pattern("^[0-9]*\\.?[0-9]{0,2}$")]],
      activo: ['true', Validators.required],
      descripcion: ['']
    });

    this.modificarPlanForm = this.fb.group({
      idPlan: [''],
      nombrePlan: ['', Validators.required],
      velocidadDescargaMbps: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
      velocidadCargaMbps: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
      wifiIncluido: [true, Validators.required],
      mesGratisPromocion: [0, [Validators.required]],
      puertosEthernet: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
      precioMensual: ['', [Validators.required, Validators.pattern("^[0-9]*\\.?[0-9]{0,2}$")]],
      activo: [true, Validators.required],
      descripcion: ['']
    });
  }

  ngOnInit(): void {
    this.cargarPlanes();
  }

  cargarPlanes(): void {
    this.errorMessage = null;
    this.planService.getPlanes().subscribe({
        next: data => {
          this.planes = data;
        },
        error: err => {
            console.error("Error al cargar planes:", err);
            this.errorMessage = "No se pudieron cargar los planes.";
        }
    });
  }

  guardarNuevoPlan(): void {
    this.errorMessage = null;
    this.mensajeExito = null;
    if (this.nuevoPlanForm.valid) {
      const formValue = this.nuevoPlanForm.value;
      const planAGuardar = {
        nombrePlan: formValue.nombrePlan,
        velocidadDescargaMbps: Number(formValue.velocidadDescargaMbps),
        velocidadCargaMbps: Number(formValue.velocidadCargaMbps),
        wifiIncluido: formValue.wifiIncluido === 'true',
        mesGratisPromocion: Number(formValue.mesGratisPromocion),
        puertosEthernet: Number(formValue.puertosEthernet),
        precioMensual: Number(formValue.precioMensual),
        activo: formValue.activo === 'true',
        descripcion: formValue.descripcion,
        fechaCreacion: new Date().toISOString()
      };

      this.planService.guardarPlan(planAGuardar as Plan).subscribe({
          next: nuevoPlanGuardado => {
              this.planes.push(nuevoPlanGuardado);
              this.nuevoPlanForm.reset({
                wifiIncluido: 'true',
                mesGratisPromocion: 0,
                activo: 'true'
              });
              this.mostrarMensajeExito("¡Plan guardado exitosamente!");
          },
          error: err => {
              console.error("Error al guardar plan:", err);
              this.errorMessage = err.error?.message || "Error al guardar el plan.";
          }
      });
    } else {
        this.errorMessage = "Por favor, complete todos los campos requeridos correctamente.";
    }
  }

  abrirModalModificar(plan: Plan): void {
    this.planSeleccionado = plan;
    this.modificarPlanForm.patchValue({
      ...plan,
      wifiIncluido: String(plan.wifiIncluido),
      mesGratisPromocion: plan.mesGratisPromocion,
      activo: String(plan.activo)
    });
  }

  actualizarPlan(): void {
    this.errorMessage = null;
    this.mensajeExito = null;
    if (this.modificarPlanForm.valid && this.planSeleccionado) {
      const formValue = this.modificarPlanForm.value;
      const planActualizado: Plan = {
          ...this.planSeleccionado,
          ...formValue,
          mesGratisPromocion: Number(formValue.mesGratisPromocion),
          wifiIncluido: formValue.wifiIncluido === 'true',
          activo: formValue.activo === 'true',
      };

      this.planService.actualizarPlan(this.planSeleccionado.idPlan, planActualizado).subscribe({
          next: planGuardado => {
              const index = this.planes.findIndex(p => p.idPlan === planGuardado.idPlan);
              if (index !== -1) {
                this.planes[index] = planGuardado;
              }
              this.mostrarMensajeExito("¡Plan actualizado exitosamente!");
          },
          error: err => {
              console.error("Error al actualizar plan:", err);
              this.errorMessage = err.error?.message || "Error al actualizar el plan.";
          }
      });
    } else {
        this.errorMessage = "El formulario de modificación no es válido.";
    }
  }

  eliminarPlan(idPlan: number): void {
    this.errorMessage = null;
    this.mensajeExito = null;
    if (confirm("¿Estás seguro de que deseas eliminar este plan?")) {
      this.planService.eliminarPlan(idPlan).subscribe({
          next: () => {
              this.planes = this.planes.filter(p => p.idPlan !== idPlan);
              this.mostrarMensajeExito("Plan eliminado.");
          },
          error: err => {
              console.error("Error al eliminar plan:", err);
              this.errorMessage = err.error?.message || "Error al eliminar el plan.";
          }
      });
    }
  }

  mostrarMensajeExito(mensaje: string): void {
    this.mensajeExito = mensaje;
    setTimeout(() => {
      this.mensajeExito = null;
    }, 3000);
  }
}