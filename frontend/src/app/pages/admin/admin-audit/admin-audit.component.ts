import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { DialogModule } from 'primeng/dialog';
import { AdminService } from '../../../services/admin.service';
import { AuditLog } from '../../../models/admin.model';

@Component({
  selector: 'app-admin-audit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, TableModule, ButtonModule, InputTextModule, DropdownModule, DialogModule],
  template: `
    <div class="admin-audit">
      <div class="header">
        <h1>Audit-Log</h1>
        <a routerLink="/admin"><button pButton label="Zurueck" icon="pi pi-arrow-left" class="p-button-text"></button></a>
      </div>

      <div class="filters">
        <p-dropdown [options]="actionOptions" [(ngModel)]="actionFilter" placeholder="Aktion filtern"
                    (onChange)="loadLogs()" [showClear]="true"></p-dropdown>
        <p-dropdown [options]="targetTypeOptions" [(ngModel)]="targetTypeFilter" placeholder="Ziel-Typ"
                    (onChange)="loadLogs()" [showClear]="true"></p-dropdown>
      </div>

      <p-table [value]="logs()" [paginator]="true" [rows]="20" [totalRecords]="totalRecords()"
               [lazy]="true" (onLazyLoad)="onPageChange($event)">
        <ng-template pTemplate="header">
          <tr>
            <th>Zeitpunkt</th>
            <th>Admin</th>
            <th>Aktion</th>
            <th>Ziel-Typ</th>
            <th>Ziel-ID</th>
            <th>IP</th>
            <th>Details</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-log>
          <tr>
            <td>{{ log.createdAt | date:'dd.MM.yyyy HH:mm:ss' }}</td>
            <td>{{ log.adminDisplayName }}</td>
            <td><span class="action-badge">{{ log.action }}</span></td>
            <td>{{ log.targetType }}</td>
            <td class="target-id">{{ log.targetId | slice:0:8 }}...</td>
            <td>{{ log.ipAddress }}</td>
            <td>
              @if (log.details) {
                <button pButton icon="pi pi-info-circle" class="p-button-text p-button-sm"
                        (click)="showDetails(log)"></button>
              } @else {
                <span>-</span>
              }
            </td>
          </tr>
        </ng-template>
      </p-table>

      <p-dialog header="Audit-Details" [(visible)]="showDetailDialog" [modal]="true" [style]="{width: '600px'}">
        @if (selectedLog()) {
          <div class="detail-content">
            <p><strong>Aktion:</strong> {{ selectedLog()!.action }}</p>
            <p><strong>Admin:</strong> {{ selectedLog()!.adminDisplayName }}</p>
            <p><strong>Zeitpunkt:</strong> {{ selectedLog()!.createdAt | date:'dd.MM.yyyy HH:mm:ss' }}</p>
            <p><strong>Ziel:</strong> {{ selectedLog()!.targetType }} / {{ selectedLog()!.targetId }}</p>
            <p><strong>IP:</strong> {{ selectedLog()!.ipAddress }}</p>
            <hr />
            <pre class="details-json">{{ selectedLog()!.details | json }}</pre>
          </div>
        }
      </p-dialog>
    </div>
  `,
  styles: [`
    .admin-audit { padding: 2rem; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .filters { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
    .action-badge { background: #e2e8f0; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.85rem; font-family: monospace; }
    .target-id { font-family: monospace; font-size: 0.85rem; }
    .details-json { background: #f5f5f5; padding: 1rem; border-radius: 4px; white-space: pre-wrap; word-break: break-word; font-size: 0.85rem; }
    .detail-content { display: flex; flex-direction: column; gap: 0.5rem; }
  `]
})
export class AdminAuditComponent implements OnInit {
  logs = signal<AuditLog[]>([]);
  totalRecords = signal(0);
  currentPage = 0;
  actionFilter: string | null = null;
  targetTypeFilter: string | null = null;
  showDetailDialog = false;
  selectedLog = signal<AuditLog | null>(null);

  actionOptions = [
    { label: 'User bearbeitet', value: 'USER_EDITED' },
    { label: 'User gesperrt', value: 'USER_SUSPENDED' },
    { label: 'User entsperrt', value: 'USER_UNSUSPENDED' },
    { label: 'User geloescht', value: 'USER_DELETED' },
    { label: 'Rolle geaendert', value: 'USER_ROLE_CHANGED' },
    { label: 'Reviews ausgeblendet', value: 'USER_REVIEWS_HIDDEN' },
    { label: 'Review ausgeblendet', value: 'REVIEW_HIDDEN' },
    { label: 'Review eingeblendet', value: 'REVIEW_SHOWN' },
    { label: 'Review geloescht', value: 'REVIEW_DELETED' },
    { label: 'Mail gesendet', value: 'MAIL_SENT' },
    { label: 'Mail mit Frist', value: 'MAIL_SENT_WITH_DEADLINE' },
    { label: 'Aktion abgebrochen', value: 'SCHEDULED_ACTION_CANCELLED' },
    { label: 'Frist verlaengert', value: 'SCHEDULED_ACTION_EXTENDED' },
    { label: 'Aktion ausgefuehrt', value: 'SCHEDULED_ACTION_EXECUTED' }
  ];

  targetTypeOptions = [
    { label: 'Benutzer', value: 'USER' },
    { label: 'Review', value: 'REVIEW' },
    { label: 'Frist-Aktion', value: 'SCHEDULED_ACTION' }
  ];

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadLogs();
  }

  loadLogs() {
    this.adminService.getAuditLogs(
      this.currentPage, 20, undefined,
      this.actionFilter || undefined,
      this.targetTypeFilter || undefined
    ).subscribe(page => {
      this.logs.set(page.content);
      this.totalRecords.set(page.totalElements);
    });
  }

  onPageChange(event: any) {
    this.currentPage = event.first / event.rows;
    this.loadLogs();
  }

  showDetails(log: AuditLog) {
    this.selectedLog.set(log);
    this.showDetailDialog = true;
  }
}
