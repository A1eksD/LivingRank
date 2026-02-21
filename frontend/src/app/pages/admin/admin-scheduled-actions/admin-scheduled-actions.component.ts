import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { CheckboxModule } from 'primeng/checkbox';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AdminService } from '../../../services/admin.service';
import { ScheduledAction } from '../../../models/admin.model';

@Component({
  selector: 'app-admin-scheduled-actions',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, TableModule, ButtonModule, TagModule,
    DialogModule, InputTextModule, CheckboxModule, ConfirmDialogModule, ToastModule],
  providers: [ConfirmationService, MessageService],
  template: `
    <div class="admin-actions">
      <div class="header">
        <h1>Frist-Aktionen</h1>
        <a routerLink="/admin"><button pButton label="Zurueck" icon="pi pi-arrow-left" class="p-button-text"></button></a>
      </div>

      <div class="filters">
        <label class="pending-filter">
          <p-checkbox [(ngModel)]="pendingOnly" [binary]="true" (onChange)="loadActions()"></p-checkbox>
          <span>Nur offene Aktionen</span>
        </label>
      </div>

      <p-table [value]="actions()" [paginator]="true" [rows]="20" [totalRecords]="totalRecords()"
               [lazy]="true" (onLazyLoad)="onPageChange($event)">
        <ng-template pTemplate="header">
          <tr>
            <th>ID</th>
            <th>Admin</th>
            <th>Ziel-Benutzer</th>
            <th>Aktion</th>
            <th>Frist</th>
            <th>Status</th>
            <th>Erstellt</th>
            <th>Aktionen</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-action>
          <tr>
            <td>{{ action.id }}</td>
            <td>{{ action.adminDisplayName }}</td>
            <td>{{ action.targetUserDisplayName }}<br/><small>{{ action.targetUserEmail }}</small></td>
            <td><span class="action-type">{{ action.actionType }}</span></td>
            <td>{{ action.deadline | date:'dd.MM.yyyy HH:mm' }}</td>
            <td>
              @if (action.executed) {
                <p-tag value="Ausgefuehrt" severity="success"></p-tag>
              } @else if (action.cancelled) {
                <p-tag value="Abgebrochen" severity="secondary"></p-tag>
              } @else {
                <p-tag value="Offen" severity="warn"></p-tag>
              }
            </td>
            <td>{{ action.createdAt | date:'dd.MM.yyyy' }}</td>
            <td class="actions">
              @if (!action.executed && !action.cancelled) {
                <button pButton icon="pi pi-clock" class="p-button-info p-button-sm"
                        pTooltip="Frist verlaengern" (click)="openExtendDialog(action)"></button>
                <button pButton icon="pi pi-times" class="p-button-danger p-button-sm"
                        pTooltip="Abbrechen" (click)="confirmCancel(action)"></button>
              }
            </td>
          </tr>
        </ng-template>
      </p-table>

      <!-- Extend Dialog -->
      <p-dialog header="Frist verlaengern" [(visible)]="showExtendDialog" [modal]="true" [style]="{width: '400px'}">
        <div class="dialog-content">
          <p>Aktion #{{ selectedAction()?.id }} - {{ selectedAction()?.actionType }}</p>
          <p>Aktuelle Frist: {{ selectedAction()?.deadline | date:'dd.MM.yyyy HH:mm' }}</p>
          <label>Neue Frist:</label>
          <input pInputText type="datetime-local" [(ngModel)]="newDeadline" class="w-full" />
        </div>
        <ng-template pTemplate="footer">
          <button pButton label="Abbrechen" class="p-button-text" (click)="showExtendDialog = false"></button>
          <button pButton label="Verlaengern" class="p-button-info" (click)="extendDeadline()" [disabled]="!newDeadline"></button>
        </ng-template>
      </p-dialog>

      <p-confirmDialog></p-confirmDialog>
      <p-toast></p-toast>
    </div>
  `,
  styles: [`
    .admin-actions { padding: 2rem; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .filters { margin-bottom: 1rem; }
    .pending-filter { display: flex; align-items: center; gap: 0.5rem; }
    .actions { display: flex; gap: 0.25rem; }
    .action-type { background: #e2e8f0; padding: 0.2rem 0.5rem; border-radius: 4px; font-family: monospace; font-size: 0.85rem; }
    .dialog-content { display: flex; flex-direction: column; gap: 0.5rem; }
    .w-full { width: 100%; }
  `]
})
export class AdminScheduledActionsComponent implements OnInit {
  actions = signal<ScheduledAction[]>([]);
  totalRecords = signal(0);
  currentPage = 0;
  pendingOnly = true;
  showExtendDialog = false;
  selectedAction = signal<ScheduledAction | null>(null);
  newDeadline = '';

  constructor(
    private adminService: AdminService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.loadActions();
  }

  loadActions() {
    this.adminService.getScheduledActions(this.currentPage, 20, this.pendingOnly).subscribe(page => {
      this.actions.set(page.content);
      this.totalRecords.set(page.totalElements);
    });
  }

  onPageChange(event: any) {
    this.currentPage = event.first / event.rows;
    this.loadActions();
  }

  openExtendDialog(action: ScheduledAction) {
    this.selectedAction.set(action);
    this.newDeadline = '';
    this.showExtendDialog = true;
  }

  extendDeadline() {
    const action = this.selectedAction();
    if (!action || !this.newDeadline) return;
    this.adminService.extendDeadline(action.id, this.newDeadline).subscribe({
      next: res => {
        this.messageService.add({ severity: 'success', summary: 'Erfolg', detail: res.message });
        this.showExtendDialog = false;
        this.loadActions();
      },
      error: err => this.messageService.add({ severity: 'error', summary: 'Fehler', detail: err.error?.message || 'Fehler' })
    });
  }

  confirmCancel(action: ScheduledAction) {
    this.confirmationService.confirm({
      message: `Frist-Aktion #${action.id} (${action.actionType}) wirklich abbrechen?`,
      header: 'Aktion abbrechen',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.adminService.cancelAction(action.id).subscribe({
          next: res => {
            this.messageService.add({ severity: 'success', summary: 'Erfolg', detail: res.message });
            this.loadActions();
          },
          error: err => this.messageService.add({ severity: 'error', summary: 'Fehler', detail: err.error?.message || 'Fehler' })
        });
      }
    });
  }
}
