import { Component } from '@angular/core';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html'
})
export class ContactComponent {
  asesorDisplay: string = "Hombre";
  asesorValue: string = "MASCULINO";

  get imagenAsesor(): string {
    return this.asesorValue === "FEMENINO" 
      ? "assets/img/woman.png" 
      : "assets/img/soporte.png";
  }

  seleccionarAsesor(value: string, display: string): void {
    this.asesorValue = value;
    this.asesorDisplay = display;
  }

  enviarMensaje(): void {
    alert('Mensaje enviado (simulación)');
  }
}