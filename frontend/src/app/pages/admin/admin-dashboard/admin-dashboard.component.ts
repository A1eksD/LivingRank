import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { AdminService } from '../../../services/admin.service';
import { AdminDashboard } from '../../../models/admin.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, CardModule, ButtonModule],
  template: `
    <div class="admin-dashboard">
      <h1>Admin Dashboard</h1>

      <div class="nav-links">
        <a routerLink="/admin/users"><button pButton label="Benutzer" icon="pi pi-users"></button></a>
        <a routerLink="/admin/reviews"><button pButton label="Reviews" icon="pi pi-star"></button></a>
        <a routerLink="/admin/mails"><button pButton label="Mailcenter" icon="pi pi-envelope"></button></a>
        <a routerLink="/admin/audit"><button pButton label="Audit-Log" icon="pi pi-list"></button></a>
        <a routerLink="/admin/scheduled-actions"><button pButton label="Frist-Aktionen" icon="pi pi-clock"></button></a>
      </div>

      @if (dashboard()) {
        <div class="stats-grid">
          <p-card header="Benutzer gesamt">
            <div class="stat-value">{{ dashboard()!.totalUsers }}</div>
          </p-card>
          <p-card header="Aktive Benutzer">
            <div class="stat-value active">{{ dashboard()!.activeUsers }}</div>
          </p-card>
          <p-card header="Gesperrte Benutzer">
            <div class="stat-value suspended">{{ dashboard()!.suspendedUsers }}</div>
          </p-card>
          <p-card header="Reviews gesamt">
            <div class="stat-value">{{ dashboard()!.totalReviews }}</div>
          </p-card>
          <p-card header="Ausgeblendete Reviews">
            <div class="stat-value warning">{{ dashboard()!.hiddenReviews }}</div>
          </p-card>
          <p-card header="Offene Frist-Aktionen">
            <div class="stat-value pending">{{ dashboard()!.pendingScheduledActions }}</div>
          </p-card>
          <p-card header="Strassen gesamt">
            <div class="stat-value">{{ dashboard()!.totalStreets }}</div>
          </p-card>
        </div>
      }
    </div>
  `,
  styles: [`
    .admin-dashboard { padding: 2rem; }
    h1 { margin-bottom: 1.5rem; }
    .nav-links { display: flex; gap: 0.5rem; margin-bottom: 2rem; flex-wrap: wrap; }
    .stats-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 1rem; }
    .stat-value { font-size: 2rem; font-weight: bold; text-align: center; padding: 1rem 0; }
    .stat-value.active { color: #22c55e; }
    .stat-value.suspended { color: #ef4444; }
    .stat-value.warning { color: #f59e0b; }
    .stat-value.pending { color: #3b82f6; }
  `]
})
export class AdminDashboardComponent implements OnInit {
  dashboard = signal<AdminDashboard | null>(null);

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.adminService.getDashboard().subscribe(data => this.dashboard.set(data));
  }
}
