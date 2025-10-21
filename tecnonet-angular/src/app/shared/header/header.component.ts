import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService, DecodedToken } from '../../services/auth.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  currentUser$: Observable<DecodedToken | null>;

  constructor(public authService: AuthService, private router: Router) {
    this.currentUser$ = this.authService.currentUser;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/iniciarSesion']);
  }
}