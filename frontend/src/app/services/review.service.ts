import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Review, ReviewRequest, PageResponse } from '../models/review.model';
import { MessageResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class ReviewService {

  constructor(private http: HttpClient) {}

  getReviewsForStreet(streetId: number, page: number = 0, size: number = 10): Observable<PageResponse<Review>> {
    return this.http.get<PageResponse<Review>>(
      `${environment.apiUrl}/streets/${streetId}/reviews`,
      { params: { page: page.toString(), size: size.toString() } }
    );
  }

  createReview(streetId: number, request: ReviewRequest): Observable<Review> {
    return this.http.post<Review>(`${environment.apiUrl}/streets/${streetId}/reviews`, request);
  }

  updateReview(streetId: number, reviewId: number, request: ReviewRequest): Observable<Review> {
    return this.http.put<Review>(`${environment.apiUrl}/streets/${streetId}/reviews/${reviewId}`, request);
  }

  deleteReview(streetId: number, reviewId: number): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${environment.apiUrl}/streets/${streetId}/reviews/${reviewId}`);
  }

  getMyReviews(page: number = 0, size: number = 10): Observable<PageResponse<Review>> {
    return this.http.get<PageResponse<Review>>(
      `${environment.apiUrl}/me/reviews`,
      { params: { page: page.toString(), size: size.toString() } }
    );
  }
}
