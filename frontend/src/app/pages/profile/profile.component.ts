import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { RatingModule } from 'primeng/rating';
import { MessageModule } from 'primeng/message';
import { TabsModule } from 'primeng/tabs';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { ReviewService } from '../../services/review.service';
import { Review } from '../../models/review.model';
import { UpdateProfileRequest } from '../../models/user.model';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule, RouterLink, CardModule, InputTextModule, ButtonModule,
            RatingModule, MessageModule, TabsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  displayName = '';
  profileImageUrl = '';
  reviews: Review[] = [];
  totalReviews = 0;
  message = '';
  error = '';
  saving = false;

  constructor(
    public authService: AuthService,
    private reviewService: ReviewService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    const user = this.authService.user();
    if (user) {
      this.displayName = user.displayName;
      this.profileImageUrl = user.profileImageUrl || '';
    }
    this.loadReviews();
  }

  loadReviews(): void {
    this.reviewService.getMyReviews(0, 50).subscribe({
      next: (page) => {
        this.reviews = page.content;
        this.totalReviews = page.totalElements;
      }
    });
  }

  saveProfile(): void {
    this.saving = true;
    this.error = '';
    this.message = '';

    const request: UpdateProfileRequest = {
      displayName: this.displayName,
      profileImageUrl: this.profileImageUrl || undefined
    };

    this.http.put<any>(`${environment.apiUrl}/me`, request).subscribe({
      next: () => {
        this.saving = false;
        this.message = 'Profil erfolgreich aktualisiert.';
        this.authService.refreshUser();
      },
      error: (err) => {
        this.saving = false;
        this.error = err.error?.message || 'Fehler beim Speichern.';
      }
    });
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('de-DE', {
      year: 'numeric', month: 'long', day: 'numeric'
    });
  }
}
