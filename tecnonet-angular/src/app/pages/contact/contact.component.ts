import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService, DecodedToken } from '../../services/auth.service';
import { SolicitudService, SolicitudRequest } from '../../services/solicitud.service';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit {
  contactForm: FormGroup;
  mensajeEnviado: boolean = false;
  errorMessage: string | null = null;
  currentUser: DecodedToken | null = null;
  isSubmitting: boolean = false; 

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private solicitudService: SolicitudService 
  ) {
     this.contactForm = this.fb.group({
       nombres: ['', Validators.required],
       apellidos: ['', Validators.required],
       correo: ['', [Validators.required, Validators.email]],
       telefono: [''],
       asunto: ['', Validators.required],
       mensaje: ['', Validators.required]
     });
  }

  ngOnInit(): void {
     this.authService.currentUser.subscribe((user: DecodedToken | null) => {
       this.currentUser = user;
       if (this.currentUser) {
         this.contactForm.patchValue({
           nombres: this.currentUser.nombre,
           apellidos: this.currentUser.apellido,
           correo: this.currentUser.sub
         });
         this.contactForm.get('nombres')?.disable();
         this.contactForm.get('apellidos')?.disable();
         this.contactForm.get('correo')?.disable();
       } else {
         this.contactForm.get('nombres')?.enable();
         this.contactForm.get('apellidos')?.enable();
         this.contactForm.get('correo')?.enable();
       }
     });
  }

  onSubmit(): void {
    this.mensajeEnviado = false;
    this.errorMessage = null;

    if (this.contactForm.invalid) {
      console.log('Formulario inválido');
      this.errorMessage = 'Por favor, complete todos los campos requeridos.';
      return;
    }

    this.isSubmitting = true;
    const formData = this.contactForm.getRawValue();

    const solicitud: SolicitudRequest = {
      asunto: formData.asunto,
      mensaje: formData.mensaje,
      nombreRemitente: this.currentUser ? this.currentUser.nombre : formData.nombres,
      apellidoRemitente: this.currentUser ? this.currentUser.apellido : formData.apellidos,
      correoRemitente: this.currentUser ? this.currentUser.sub : formData.correo,
      telefonoRemitente: formData.telefono
    };

    console.log('Enviando solicitud:', solicitud);

    this.solicitudService.enviarSolicitud(solicitud).subscribe({
      next: (response) => {
        console.log('Respuesta del servidor:', response);
        this.mensajeEnviado = true;
        this.contactForm.patchValue({
          telefono: '',
          asunto: '',
          mensaje: ''
        });
        if (!this.currentUser) {
           this.contactForm.patchValue({
             nombres: '',
             apellidos: '',
             correo: ''
           });
        }
        this.isSubmitting = false;
      },
      error: (err) => {
        console.error('Error al enviar solicitud:', err);
        this.errorMessage = 'Hubo un error al enviar el mensaje. Inténtalo más tarde.';
        this.isSubmitting = false;
      }
    });
  }
}