import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { RatingModule } from 'primeng/rating';
import { ButtonModule } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { PaginatorModule } from 'primeng/paginator';
import { StreetService } from '../../services/street.service';
import { ReviewService } from '../../services/review.service';
import { AuthService } from '../../services/auth.service';
import { StreetDetail } from '../../models/street.model';
import { Review, CRITERIA_LABELS, PageResponse } from '../../models/review.model';

@Component({
  selector: 'app-street-detail',
  imports: [CommonModule, FormsModule, RouterLink, CardModule, RatingModule, ButtonModule,
            ProgressBarModule, ProgressSpinnerModule, PaginatorModule],
  templateUrl: './street-detail.component.html',
  styleUrl: './street-detail.component.scss'
})
export class StreetDetailComponent implements OnInit {
  streetDetail: StreetDetail | null = null;
  reviews: Review[] = [];
  totalReviews = 0;
  currentPage = 0;
  pageSize = 10;
  loading = true;
  streetId!: number;
  criteriaLabels = CRITERIA_LABELS;

  constructor(
    private route: ActivatedRoute,
    private streetService: StreetService,
    private reviewService: ReviewService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.streetId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadStreetDetail();
    this.loadReviews();
  }

  loadStreetDetail(): void {
    this.streetService.getStreetDetail(this.streetId).subscribe({
      next: (detail) => {
        this.streetDetail = detail;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadReviews(): void {
    this.reviewService.getReviewsForStreet(this.streetId, this.currentPage, this.pageSize).subscribe({
      next: (page) => {
        this.reviews = page.content;
        this.totalReviews = page.totalElements;
      }
    });
  }

  onPageChange(event: any): void {
    this.currentPage = event.page;
    this.loadReviews();
  }

  getCriteriaEntries(): { key: string; label: string; value: number | null }[] {
    if (!this.streetDetail?.criteriaAverages) return [];
    const averages = this.streetDetail.criteriaAverages as any;
    return Object.keys(this.criteriaLabels).map(key => ({
      key,
      label: this.criteriaLabels[key],
      value: averages[key] ? Math.round(averages[key] * 10) / 10 : null
    })).filter(entry => entry.value !== null);
  }

  getProgressValue(value: number | null): number {
    return value ? (value / 5) * 100 : 0;
  }

  canWriteReview(): boolean {
    return this.authService.loggedIn() && !!this.streetDetail && !this.streetDetail.userHasReviewed;
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('de-DE', {
      year: 'numeric', month: 'long', day: 'numeric'
    });
  }
}
