import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ReviewService } from './review.service';
import { environment } from '../../environments/environment';

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ReviewService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get reviews for street', () => {
    const mockPage = {
      content: [{ id: 1, streetId: 1, overallRating: 4, createdAt: '2024-01-01', updatedAt: '2024-01-01' }],
      totalElements: 1, totalPages: 1, number: 0, size: 10
    };

    service.getReviewsForStreet(1, 0, 10).subscribe(page => {
      expect(page.content.length).toBe(1);
      expect(page.totalElements).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/streets/1/reviews?page=0&size=10`);
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);
  });

  it('should create review', () => {
    const request = { overallRating: 4, comment: 'Great neighborhood' };
    const mockResponse = { id: 1, streetId: 1, overallRating: 4, comment: 'Great neighborhood', createdAt: '2024-01-01', updatedAt: '2024-01-01' };

    service.createReview(1, request).subscribe(review => {
      expect(review.overallRating).toBe(4);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/streets/1/reviews`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should delete review', () => {
    service.deleteReview(1, 1).subscribe(response => {
      expect(response.message).toContain('gelöscht');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/streets/1/reviews/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ message: 'Bewertung erfolgreich gelöscht.' });
  });

  it('should get my reviews', () => {
    const mockPage = { content: [], totalElements: 0, totalPages: 0, number: 0, size: 10 };

    service.getMyReviews(0, 10).subscribe(page => {
      expect(page.content.length).toBe(0);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/me/reviews?page=0&size=10`);
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);
  });
});
