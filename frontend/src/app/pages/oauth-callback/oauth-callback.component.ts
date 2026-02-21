import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-oauth-callback',
  imports: [ProgressSpinnerModule],
  template: `
    <div class="callback-container">
      <p-progressSpinner />
      <p>Anmeldung wird verarbeitet...</p>
    </div>
  `,
  styles: [`
    .callback-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: calc(100vh - 70px);
      gap: 1rem;
    }
  `]
})
export class OAuthCallbackComponent implements OnInit {

  constructor(private route: ActivatedRoute, private authService: AuthService) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParams['token'];
    if (token) {
      this.authService.handleOAuthCallback(token);
    }
  }
}
