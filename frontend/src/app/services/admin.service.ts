import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  AdminDashboard,
  AdminMail,
  AdminMailRequest,
  AdminReview,
  AdminUser,
  AdminUserUpdateRequest,
  AuditLog,
  Page,
  ScheduledAction
} from '../models/admin.model';
import { MessageResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AdminService {

  private readonly baseUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  // Dashboard
  getDashboard(): Observable<AdminDashboard> {
    return this.http.get<AdminDashboard>(`${this.baseUrl}/dashboard`);
  }

  // Users
  getUsers(page = 0, size = 20, status?: string, search?: string): Observable<Page<AdminUser>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    if (search) params = params.set('search', search);
    return this.http.get<Page<AdminUser>>(`${this.baseUrl}/users`, { params });
  }

  getUser(userId: string): Observable<AdminUser> {
    return this.http.get<AdminUser>(`${this.baseUrl}/users/${userId}`);
  }

  updateUser(userId: string, request: AdminUserUpdateRequest): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${this.baseUrl}/users/${userId}`, request);
  }

  suspendUser(userId: string, reason: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.baseUrl}/users/${userId}/suspend`, { reason });
  }

  unsuspendUser(userId: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.baseUrl}/users/${userId}/unsuspend`, {});
  }

  deleteUser(userId: string): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.baseUrl}/users/${userId}`);
  }

  changeRole(userId: string, role: string): Observable<MessageResponse> {
    return this.http.put<MessageResponse>(`${this.baseUrl}/users/${userId}/role`, { role });
  }

  // Reviews
  getReviews(page = 0, size = 20, streetId?: number, userId?: string, visible?: boolean): Observable<Page<AdminReview>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (streetId != null) params = params.set('streetId', streetId);
    if (userId) params = params.set('userId', userId);
    if (visible != null) params = params.set('visible', visible);
    return this.http.get<Page<AdminReview>>(`${this.baseUrl}/reviews`, { params });
  }

  getReview(reviewId: number): Observable<AdminReview> {
    return this.http.get<AdminReview>(`${this.baseUrl}/reviews/${reviewId}`);
  }

  hideReview(reviewId: number): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.baseUrl}/reviews/${reviewId}/hide`, {});
  }

  showReview(reviewId: number): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.baseUrl}/reviews/${reviewId}/show`, {});
  }

  deleteReview(reviewId: number): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.baseUrl}/reviews/${reviewId}`);
  }

  // Mails
  sendMail(request: AdminMailRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.baseUrl}/mails`, request);
  }

  getMails(page = 0, size = 20, recipientId?: string): Observable<Page<AdminMail>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (recipientId) params = params.set('recipientId', recipientId);
    return this.http.get<Page<AdminMail>>(`${this.baseUrl}/mails`, { params });
  }

  getMail(mailId: number): Observable<AdminMail> {
    return this.http.get<AdminMail>(`${this.baseUrl}/mails/${mailId}`);
  }

  // Audit Logs
  getAuditLogs(page = 0, size = 20, adminId?: string, action?: string, targetType?: string,
               from?: string, to?: string): Observable<Page<AuditLog>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (adminId) params = params.set('adminId', adminId);
    if (action) params = params.set('action', action);
    if (targetType) params = params.set('targetType', targetType);
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.http.get<Page<AuditLog>>(`${this.baseUrl}/audit`, { params });
  }

  // Scheduled Actions
  getScheduledActions(page = 0, size = 20, pendingOnly = false): Observable<Page<ScheduledAction>> {
    const params = new HttpParams().set('page', page).set('size', size).set('pendingOnly', pendingOnly);
    return this.http.get<Page<ScheduledAction>>(`${this.baseUrl}/scheduled-actions`, { params });
  }

  cancelAction(actionId: number): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.baseUrl}/scheduled-actions/${actionId}/cancel`, {});
  }

  extendDeadline(actionId: number, newDeadline: string): Observable<MessageResponse> {
    return this.http.put<MessageResponse>(`${this.baseUrl}/scheduled-actions/${actionId}/extend`, { newDeadline });
  }
}
