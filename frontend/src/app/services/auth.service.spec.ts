import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start as not logged in', () => {
    expect(service.loggedIn()).toBeFalse();
    expect(service.user()).toBeNull();
  });

  it('should register and return message', () => {
    const request = { email: 'test@test.com', password: 'Password123', displayName: 'Test' };

    service.register(request).subscribe(response => {
      expect(response.message).toContain('Bestätigungsmail');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);
    expect(req.request.method).toBe('POST');
    req.flush({ message: 'Wenn die E-Mail-Adresse gültig ist, wurde eine Bestätigungsmail gesendet.' });
  });

  it('should login and set token/user', () => {
    const loginRequest = { email: 'test@test.com', password: 'Password123' };
    const mockUser = { id: '123', email: 'test@test.com', displayName: 'Test', authProvider: 'LOCAL', emailVerified: true, profileImageUrl: null };

    service.login(loginRequest).subscribe(response => {
      expect(response.token).toBe('jwt-token');
      expect(service.loggedIn()).toBeTrue();
      expect(service.user()?.email).toBe('test@test.com');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(loginRequest);
    req.flush({ token: 'jwt-token', type: 'Bearer', user: mockUser });
  });

  it('should verify email', () => {
    service.verifyEmail('token123').subscribe(response => {
      expect(response.message).toContain('bestätigt');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/verify-email?token=token123`);
    expect(req.request.method).toBe('GET');
    req.flush({ message: 'E-Mail-Adresse erfolgreich bestätigt.' });
  });

  it('should logout and clear state', () => {
    localStorage.setItem('lr_token', 'test-token');
    localStorage.setItem('lr_user', '{"id":"123"}');

    service.logout();

    expect(service.loggedIn()).toBeFalse();
    expect(service.user()).toBeNull();
    expect(localStorage.getItem('lr_token')).toBeNull();
  });

  it('should return token from localStorage', () => {
    localStorage.setItem('lr_token', 'stored-token');

    // Create new service to pick up localStorage
    const newService = TestBed.inject(AuthService);
    expect(newService.getToken()).toBe('stored-token');
  });
});
