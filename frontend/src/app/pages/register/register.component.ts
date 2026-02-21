import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/user.model';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink, CardModule, InputTextModule, PasswordModule, ButtonModule, MessageModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  request: RegisterRequest = { email: '', password: '', displayName: '' };
  message = '';
  error = '';
  loading = false;
  registered = false;

  constructor(private authService: AuthService) {}

  register(): void {
    this.loading = true;
    this.error = '';
    this.message = '';

    this.authService.register(this.request).subscribe({
      next: (response) => {
        this.loading = false;
        this.registered = true;
        this.message = response.message;
      },
      error: (err) => {
        this.loading = false;
        if (err.error && typeof err.error === 'object' && !err.error.message) {
          const errors = Object.values(err.error).join(', ');
          this.error = errors || 'Registrierung fehlgeschlagen.';
        } else {
          this.error = err.error?.message || 'Registrierung fehlgeschlagen.';
        }
      }
    });
  }
}
