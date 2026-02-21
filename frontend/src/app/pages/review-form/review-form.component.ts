import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { RatingModule } from 'primeng/rating';
import { ButtonModule } from 'primeng/button';
import { TextareaModule } from 'primeng/textarea';
import { MessageModule } from 'primeng/message';
import { ReviewService } from '../../services/review.service';
import { StreetService } from '../../services/street.service';
import { ReviewRequest, CRITERIA_LABELS } from '../../models/review.model';
import { Street } from '../../models/street.model';

@Component({
  selector: 'app-review-form',
  imports: [CommonModule, FormsModule, CardModule, RatingModule, ButtonModule, TextareaModule, MessageModule],
  templateUrl: './review-form.component.html',
  styleUrl: './review-form.component.scss'
})
export class ReviewFormComponent implements OnInit {
  streetId!: number;
  street: Street | null = null;
  criteriaLabels = CRITERIA_LABELS;
  criteriaKeys = Object.keys(CRITERIA_LABELS);

  review: ReviewRequest = {
    overallRating: 0,
    comment: ''
  };

  criteria: Record<string, number> = {};
  submitting = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reviewService: ReviewService,
    private streetService: StreetService
  ) {
    for (const key of this.criteriaKeys) {
      this.criteria[key] = 0;
    }
  }

  ngOnInit(): void {
    this.streetId = Number(this.route.snapshot.paramMap.get('id'));
    this.streetService.getStreetDetail(this.streetId).subscribe({
      next: (detail) => {
        this.street = detail.street;
        if (detail.userHasReviewed) {
          this.router.navigate(['/streets', this.streetId]);
        }
      }
    });
  }

  submit(): void {
    if (!this.review.overallRating || this.review.overallRating < 1) {
      this.error = 'Bitte geben Sie eine Gesamtbewertung ab.';
      return;
    }

    this.submitting = true;
    this.error = '';

    const request: ReviewRequest = {
      overallRating: this.review.overallRating,
      comment: this.review.comment,
      ...this.buildCriteria()
    };

    this.reviewService.createReview(this.streetId, request).subscribe({
      next: () => {
        this.router.navigate(['/streets', this.streetId]);
      },
      error: (err) => {
        this.submitting = false;
        if (err.status === 409) {
          this.error = 'Sie haben diese Straße bereits bewertet.';
        } else {
          this.error = 'Ein Fehler ist aufgetreten. Bitte versuchen Sie es erneut.';
        }
      }
    });
  }

  private buildCriteria(): Partial<ReviewRequest> {
    const result: any = {};
    for (const key of this.criteriaKeys) {
      if (this.criteria[key] && this.criteria[key] > 0) {
        result[key] = this.criteria[key];
      }
    }
    return result;
  }

  get commentLength(): number {
    return this.review.comment?.length || 0;
  }
}
