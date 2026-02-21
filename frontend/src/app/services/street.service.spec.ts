import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { StreetService } from './street.service';
import { environment } from '../../environments/environment';

describe('StreetService', () => {
  let service: StreetService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StreetService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(StreetService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should search streets', () => {
    const mockStreets = [
      { id: 1, streetName: 'Berliner Str.', postalCode: '10115', city: 'Berlin', country: 'DE', averageRating: 4.2, reviewCount: 5 }
    ];

    service.searchStreets('Berlin').subscribe(streets => {
      expect(streets.length).toBe(1);
      expect(streets[0].streetName).toBe('Berliner Str.');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/streets/search?q=Berlin`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStreets);
  });

  it('should get street detail', () => {
    const mockDetail = {
      street: { id: 1, streetName: 'Test', postalCode: '12345', city: 'Berlin', country: 'DE', averageRating: 3.5, reviewCount: 3 },
      criteriaAverages: {},
      userHasReviewed: false
    };

    service.getStreetDetail(1).subscribe(detail => {
      expect(detail.street.streetName).toBe('Test');
      expect(detail.userHasReviewed).toBeFalse();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/streets/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockDetail);
  });
});
