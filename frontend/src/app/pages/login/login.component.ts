import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';
import { DividerModule } from 'primeng/divider';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/user.model';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink, CardModule, InputTextModule, PasswordModule, ButtonModule, MessageModule, DividerModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  credentials: LoginRequest = { email: '', password: '' };
  error = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.loading = true;
    this.error = '';

    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'E-Mail oder Passwort ist falsch.';
      }
    });
  }

  loginWithGoogle(): void {
    this.authService.loginWithGoogle();
  }
}
