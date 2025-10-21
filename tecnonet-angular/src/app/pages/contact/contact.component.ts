import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService, DecodedToken } from '../../services/auth.service';

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

  constructor(
    private fb: FormBuilder,
    private authService: AuthService
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
      }
    });
  }

  onSubmit(): void {
    this.mensajeEnviado = false;
    this.errorMessage = null;

    if (this.contactForm.valid) {
      console.log('Formulario enviado:', this.contactForm.value);
      this.mensajeEnviado = true;
      this.contactForm.patchValue({
          telefono: '',
          asunto: '',
          mensaje: ''
      });

    } else {
      console.log('Formulario inválido');
      this.errorMessage = 'Por favor, complete todos los campos requeridos.';
    }
  }
}