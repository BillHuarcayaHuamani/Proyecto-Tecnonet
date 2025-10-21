import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { SolicitudService } from '../../services/solicitud.service';
import { Solicitud } from '../../models/solicitud.model';
import { FormsModule } from '@angular/forms';
import { RespuestaService } from '../../services/respuesta.service';

@Component({
  selector: 'app-message-list',
  standalone: true,
  imports: [CommonModule, DatePipe, FormsModule],
  templateUrl: './message-list.component.html',
  styleUrls: ['./message-list.component.css']
})
export class MessageListComponent implements OnInit {

  mensajes: Solicitud[] = [];
  errorMessage: string | null = null;
  mensajeExito: string | null = null;
  solicitudSeleccionada: Solicitud | null = null;
  respuestaTexto: string = '';

  constructor(
    private solicitudService: SolicitudService,
    private respuestaService: RespuestaService 
  ) { }

  ngOnInit(): void {
    this.cargarMensajes();
  }

  cargarMensajes(): void {
    this.solicitudService.getSolicitudes().subscribe({
      next: (data) => {
        this.mensajes = data;
      },
      error: (err) => {
        console.error("Error al cargar mensajes:", err);
        this.errorMessage = "No se pudieron cargar los mensajes.";
      }
    });
  }

  abrirModalRespuesta(solicitud: Solicitud): void {
    this.solicitudSeleccionada = solicitud;
    this.respuestaTexto = '';
    this.errorMessage = null;
    this.mensajeExito = null;
  }

  enviarRespuesta(): void {
    if (!this.solicitudSeleccionada || !this.respuestaTexto.trim()) {
      this.errorMessage = "La respuesta no puede estar vacía.";
      return;
    }

    const payload = { respuesta: this.respuestaTexto };
    this.respuestaService.guardarRespuesta(this.solicitudSeleccionada.idSolicitud, payload)
      .subscribe({
        next: (response) => {
          this.mensajeExito = "Respuesta enviada exitosamente.";
          this.solicitudSeleccionada = null; 

          const backdrop = document.querySelector('.modal-backdrop');
          if (backdrop) {
            backdrop.remove();
          }
          document.body.classList.remove('modal-open');
          document.body.style.overflow = '';
          document.body.style.paddingRight = '';
        },
        error: (err) => {
          console.error("Error al enviar respuesta:", err);
          this.errorMessage = err.error?.message || "Error al enviar la respuesta.";
        }
      });
  }

  cerrarModal(): void {
      this.solicitudSeleccionada = null;
  }
}