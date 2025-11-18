import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  
  passwordFieldType: 'password' | 'text' = 'password';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    this.errorMessage = null;
    this.successMessage = null;

    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          this.successMessage = response.message;
          if (this.authService.hasRole('Administrador')) {
            this.router.navigate(['/admin']);
          } else if (this.authService.hasRole('Operario')) {
            this.router.navigate(['/admin/dashboard-operario']);
          } else if (this.authService.hasRole('Cliente')) {
            this.router.navigate(['/home']);
          } else {
            this.router.navigate(['/home']);
          }
        },

        error: (err) => {
          this.errorMessage = err.error?.message || 'Error en el servidor.';
          console.error('Error en el login:', err);
        }
      });
    } else {
      this.errorMessage = 'Por favor, completa todos los campos correctamente.';
    }
  }

  togglePasswordVisibility(): void {
    this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
  }
}