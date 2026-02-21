import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CardModule } from 'primeng/card';
import { MessageModule } from 'primeng/message';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-verify-email',
  imports: [RouterLink, CardModule, MessageModule, ProgressSpinnerModule],
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.scss'
})
export class VerifyEmailComponent implements OnInit {
  loading = true;
  message = '';
  error = '';

  constructor(private route: ActivatedRoute, private authService: AuthService) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParams['token'];
    if (token) {
      this.authService.verifyEmail(token).subscribe({
        next: (response) => {
          this.loading = false;
          this.message = response.message;
        },
        error: (err) => {
          this.loading = false;
          this.error = err.error?.message || 'Ungültiger oder abgelaufener Bestätigungslink.';
        }
      });
    } else {
      this.loading = false;
      this.error = 'Kein Bestätigungstoken gefunden.';
    }
  }
}
