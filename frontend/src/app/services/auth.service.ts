import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthResponse, LoginRequest, MessageResponse, RegisterRequest, User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'lr_token';
  private readonly USER_KEY = 'lr_user';

  private currentUser = signal<User | null>(this.loadUserFromStorage());
  private isAuthenticated = signal<boolean>(!!this.getToken());

  user = this.currentUser.asReadonly();
  loggedIn = this.isAuthenticated.asReadonly();

  constructor(private http: HttpClient, private router: Router) {}

  register(request: RegisterRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${environment.apiUrl}/auth/register`, request);
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, request).pipe(
      tap(response => {
        this.setSession(response.token, response.user);
      })
    );
  }

  verifyEmail(token: string): Observable<MessageResponse> {
    return this.http.get<MessageResponse>(`${environment.apiUrl}/auth/verify-email`, {
      params: { token }
    });
  }

  loginWithGoogle(): void {
    window.location.href = environment.googleOAuthUrl;
  }

  handleOAuthCallback(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    this.isAuthenticated.set(true);
    // Fetch user profile
    this.http.get<User>(`${environment.apiUrl}/me`).subscribe({
      next: (user) => {
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
        this.currentUser.set(user);
        this.router.navigate(['/']);
      },
      error: () => {
        this.logout();
      }
    });
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
    this.router.navigate(['/']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  refreshUser(): void {
    this.http.get<User>(`${environment.apiUrl}/me`).subscribe({
      next: (user) => {
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
        this.currentUser.set(user);
      }
    });
  }

  private setSession(token: string, user: User): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
    this.isAuthenticated.set(true);
  }

  private loadUserFromStorage(): User | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    if (userStr) {
      try {
        return JSON.parse(userStr);
      } catch {
        return null;
      }
    }
    return null;
  }
}
