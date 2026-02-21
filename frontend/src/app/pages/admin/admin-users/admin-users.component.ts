import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { TagModule } from 'primeng/tag';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AdminService } from '../../../services/admin.service';
import { AdminUser, Page } from '../../../models/admin.model';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, TableModule, ButtonModule, InputTextModule,
    DropdownModule, DialogModule, InputTextareaModule, TagModule, ConfirmDialogModule, ToastModule],
  providers: [ConfirmationService, MessageService],
  template: `
    <div class="admin-users">
      <div class="header">
        <h1>Benutzerverwaltung</h1>
        <a routerLink="/admin"><button pButton label="Zurueck" icon="pi pi-arrow-left" class="p-button-text"></button></a>
      </div>

      <div class="filters">
        <input pInputText [(ngModel)]="searchTerm" placeholder="Suche nach E-Mail oder Name..." (keyup.enter)="loadUsers()" />
        <p-dropdown [options]="statusOptions" [(ngModel)]="statusFilter" placeholder="Status filtern"
                    (onChange)="loadUsers()" [showClear]="true"></p-dropdown>
        <button pButton label="Suchen" icon="pi pi-search" (click)="loadUsers()"></button>
      </div>

      <p-table [value]="users()" [paginator]="true" [rows]="20" [totalRecords]="totalRecords()"
               [lazy]="true" (onLazyLoad)="onPageChange($event)">
        <ng-template pTemplate="header">
          <tr>
            <th>Name</th>
            <th>E-Mail</th>
            <th>Rolle</th>
            <th>Status</th>
            <th>Registriert</th>
            <th>Aktionen</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-user>
          <tr>
            <td>{{ user.displayName }}</td>
            <td>{{ user.email }}</td>
            <td><p-tag [value]="user.role" [severity]="getRoleSeverity(user.role)"></p-tag></td>
            <td><p-tag [value]="user.status" [severity]="getStatusSeverity(user.status)"></p-tag></td>
            <td>{{ user.createdAt | date:'dd.MM.yyyy HH:mm' }}</td>
            <td class="actions">
              @if (user.status === 'ACTIVE') {
                <button pButton icon="pi pi-ban" class="p-button-warning p-button-sm"
                        pTooltip="Sperren" (click)="openSuspendDialog(user)"></button>
              }
              @if (user.status === 'SUSPENDED') {
                <button pButton icon="pi pi-check" class="p-button-success p-button-sm"
                        pTooltip="Entsperren" (click)="unsuspendUser(user)"></button>
              }
              <button pButton icon="pi pi-envelope" class="p-button-info p-button-sm"
                      pTooltip="Mail senden" (click)="openMailDialog(user)"></button>
              @if (isSuperAdmin()) {
                <button pButton icon="pi pi-user-edit" class="p-button-secondary p-button-sm"
                        pTooltip="Rolle aendern" (click)="openRoleDialog(user)"></button>
                <button pButton icon="pi pi-trash" class="p-button-danger p-button-sm"
                        pTooltip="Loeschen" (click)="confirmDeleteUser(user)"></button>
              }
            </td>
          </tr>
        </ng-template>
      </p-table>

      <!-- Suspend Dialog -->
      <p-dialog header="Benutzer sperren" [(visible)]="showSuspendDialog" [modal]="true" [style]="{width: '450px'}">
        <div class="dialog-content">
          <p>Benutzer: <strong>{{ selectedUser()?.displayName }}</strong></p>
          <label>Grund der Sperrung:</label>
          <textarea pInputTextarea [(ngModel)]="suspendReason" rows="3" class="w-full"></textarea>
        </div>
        <ng-template pTemplate="footer">
          <button pButton label="Abbrechen" class="p-button-text" (click)="showSuspendDialog = false"></button>
          <button pButton label="Sperren" class="p-button-warning" (click)="suspendUser()" [disabled]="!suspendReason"></button>
        </ng-template>
      </p-dialog>

      <!-- Mail Dialog -->
      <p-dialog header="E-Mail senden" [(visible)]="showMailDialog" [modal]="true" [style]="{width: '550px'}">
        <div class="dialog-content">
          <p>An: <strong>{{ selectedUser()?.email }}</strong></p>
          <label>Betreff:</label>
          <input pInputText [(ngModel)]="mailSubject" class="w-full" />
          <label>Nachricht:</label>
          <textarea pInputTextarea [(ngModel)]="mailBody" rows="5" class="w-full"></textarea>
          <label>Frist setzen (optional):</label>
          <input pInputText type="datetime-local" [(ngModel)]="mailDeadline" class="w-full" />
          @if (mailDeadline) {
            <label>Aktion nach Fristablauf:</label>
            <p-dropdown [options]="deadlineActions" [(ngModel)]="mailDeadlineAction" placeholder="Aktion waehlen"></p-dropdown>
          }
        </div>
        <ng-template pTemplate="footer">
          <button pButton label="Abbrechen" class="p-button-text" (click)="showMailDialog = false"></button>
          <button pButton label="Senden" class="p-button-info" (click)="sendMail()"
                  [disabled]="!mailSubject || !mailBody"></button>
        </ng-template>
      </p-dialog>

      <!-- Role Dialog -->
      <p-dialog header="Rolle aendern" [(visible)]="showRoleDialog" [modal]="true" [style]="{width: '400px'}">
        <div class="dialog-content">
          <p>Benutzer: <strong>{{ selectedUser()?.displayName }}</strong></p>
          <p>Aktuelle Rolle: <p-tag [value]="selectedUser()?.role || ''" [severity]="getRoleSeverity(selectedUser()?.role || '')"></p-tag></p>
          <label>Neue Rolle:</label>
          <p-dropdown [options]="roleOptions" [(ngModel)]="newRole" placeholder="Rolle waehlen"></p-dropdown>
        </div>
        <ng-template pTemplate="footer">
          <button pButton label="Abbrechen" class="p-button-text" (click)="showRoleDialog = false"></button>
          <button pButton label="Aendern" class="p-button-primary" (click)="changeRole()" [disabled]="!newRole"></button>
        </ng-template>
      </p-dialog>

      <p-confirmDialog></p-confirmDialog>
      <p-toast></p-toast>
    </div>
  `,
  styles: [`
    .admin-users { padding: 2rem; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .filters { display: flex; gap: 0.5rem; margin-bottom: 1rem; flex-wrap: wrap; }
    .filters input { min-width: 250px; }
    .actions { display: flex; gap: 0.25rem; }
    .dialog-content { display: flex; flex-direction: column; gap: 0.5rem; }
    .w-full { width: 100%; }
  `]
})
export class AdminUsersComponent implements OnInit {
  users = signal<AdminUser[]>([]);
  totalRecords = signal(0);
  currentPage = 0;
  searchTerm = '';
  statusFilter: string | null = null;

  selectedUser = signal<AdminUser | null>(null);
  showSuspendDialog = false;
  showMailDialog = false;
  showRoleDialog = false;
  suspendReason = '';
  mailSubject = '';
  mailBody = '';
  mailDeadline = '';
  mailDeadlineAction = '';
  newRole = '';

  statusOptions = [
    { label: 'Aktiv', value: 'ACTIVE' },
    { label: 'Gesperrt', value: 'SUSPENDED' },
    { label: 'Geloescht', value: 'DELETED' }
  ];

  roleOptions = [
    { label: 'User', value: 'USER' },
    { label: 'Admin', value: 'ADMIN' },
    { label: 'Super Admin', value: 'SUPER_ADMIN' }
  ];

  deadlineActions = [
    { label: 'Benutzer sperren', value: 'SUSPEND' },
    { label: 'Benutzer loeschen', value: 'DELETE' },
    { label: 'Reviews ausblenden', value: 'HIDE_REVIEWS' }
  ];

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.adminService.getUsers(this.currentPage, 20, this.statusFilter || undefined, this.searchTerm || undefined)
      .subscribe(page => {
        this.users.set(page.content);
        this.totalRecords.set(page.totalElements);
      });
  }

  onPageChange(event: any) {
    this.currentPage = event.first / event.rows;
    this.loadUsers();
  }

  isSuperAdmin(): boolean {
    return this.authService.user()?.role === 'SUPER_ADMIN';
  }

  getRoleSeverity(role: string): "success" | "info" | "warn" | "danger" | "secondary" | "contrast" | undefined {
    switch (role) {
      case 'SUPER_ADMIN': return 'danger';
      case 'ADMIN': return 'warn';
      default: return 'info';
    }
  }

  getStatusSeverity(status: string): "success" | "info" | "warn" | "danger" | "secondary" | "contrast" | undefined {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'SUSPENDED': return 'warn';
      case 'DELETED': return 'danger';
      default: return 'info';
    }
  }

  openSuspendDialog(user: AdminUser) {
    this.selectedUser.set(user);
    this.suspendReason = '';
    this.showSuspendDialog = true;
  }

  suspendUser() {
    const user = this.selectedUser();
    if (!user) return;
    this.adminService.suspendUser(user.id, this.suspendReason).subscribe({
      next: res => {
        this.messageService.add({ severity: 'success', summary: 'Erfolg', detail: res.message });
        this.showSuspendDialog = false;
        this.loadUsers();
      },
      error: err => this.messageService.add({ severity: 'error', summary: 'Fehler', detail: err.error?.message || 'Fehler' })
    });
  }

  unsuspendUser(user: AdminUser) {
    this.adminService.unsuspendUser(user.id).subscribe({
      next: res => {
        this.messageService.add({ severity: 'success', summary: 'Erfolg', detail: res.message });
        this.loadUsers();
      },
      error: err => this.messageService.add({ severity: 'error', summary: 'Fehler', detail: err.error?.message || 'Fehler' })
    });
  }

  confirmDeleteUser(user: AdminUser) {
    this.confirmationService.confirm({
      message: `Benutzer "${user.displayName}" wirklich loeschen?`,
      header: 'Benutzer loeschen',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.adminService.deleteUser(user.id).subscribe({
          next: res => {
            this.messageService.add({ severity: 'success', summary: 'Erfolg', detail: res.message });
            this.loadUsers();
          },
          error: err => this.messageService.add({ severity: 'error', summary: 'Fehler', detail: err.error?.message || 'Fehler' })
        });
      }
    });
  }

  openMailDialog(user: AdminUser) {
    this.selectedUser.set(user);
    this.mailSubject = '';
    this.mailBody = '';
    this.mailDeadline = '';
    this.mailDeadlineAction = '';
    this.showMailDialog = true;
  }

  sendMail() {
    const user = this.selectedUser();
    if (!user) return;
    this.adminService.sendMail({
      recipientId: user.id,
      subject: this.mailSubject,
      body: this.mailBody,
      deadline: this.mailDeadline || undefined,
      deadlineAction: this.mailDeadlineAction || undefined
    }).subscribe({
      next: res => {
        this.messageService.add({ severity: 'success', summary: 'Erfolg', detail: res.message });
        this.showMailDialog = false;
      },
      error: err => this.messageService.add({ severity: 'error', summary: 'Fehler', detail: err.error?.message || 'Fehler' })
    });
  }

  openRoleDialog(user: AdminUser) {
    this.selectedUser.set(user);
    this.newRole = '';
    this.showRoleDialog = true;
  }

  changeRole() {
    const user = this.selectedUser();
    if (!user || !this.newRole) return;
    this.adminService.changeRole(user.id, this.newRole).subscribe({
      next: res => {
        this.messageService.add({ severity: 'success', summary: 'Erfolg', detail: res.message });
        this.showRoleDialog = false;
        this.loadUsers();
      },
      error: err => this.messageService.add({ severity: 'error', summary: 'Fehler', detail: err.error?.message || 'Fehler' })
    });
  }
}
