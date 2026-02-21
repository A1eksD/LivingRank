import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { AdminService } from '../../../services/admin.service';
import { AdminMail } from '../../../models/admin.model';

@Component({
  selector: 'app-admin-mails',
  standalone: true,
  imports: [CommonModule, RouterLink, TableModule, ButtonModule, TagModule, DialogModule],
  template: `
    <div class="admin-mails">
      <div class="header">
        <h1>Mailcenter</h1>
        <a routerLink="/admin"><button pButton label="Zurueck" icon="pi pi-arrow-left" class="p-button-text"></button></a>
      </div>

      <p-table [value]="mails()" [paginator]="true" [rows]="20" [totalRecords]="totalRecords()"
               [lazy]="true" (onLazyLoad)="onPageChange($event)">
        <ng-template pTemplate="header">
          <tr>
            <th>ID</th>
            <th>Gesendet von</th>
            <th>Empfaenger</th>
            <th>Betreff</th>
            <th>Frist</th>
            <th>Gesendet am</th>
            <th>Aktionen</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-mail>
          <tr>
            <td>{{ mail.id }}</td>
            <td>{{ mail.adminDisplayName }}</td>
            <td>{{ mail.recipientDisplayName }}<br/><small>{{ mail.recipientEmail }}</small></td>
            <td>{{ mail.subject }}</td>
            <td>
              @if (mail.hasDeadline) {
                <p-tag [value]="mail.deadlineAction" severity="warn"></p-tag>
              } @else {
                <span>-</span>
              }
            </td>
            <td>{{ mail.sentAt | date:'dd.MM.yyyy HH:mm' }}</td>
            <td>
              <button pButton icon="pi pi-eye" class="p-button-info p-button-sm"
                      pTooltip="Details" (click)="showMailDetail(mail)"></button>
            </td>
          </tr>
        </ng-template>
      </p-table>

      <p-dialog header="Mail-Details" [(visible)]="showDetail" [modal]="true" [style]="{width: '600px'}">
        @if (selectedMail()) {
          <div class="mail-detail">
            <p><strong>Von:</strong> {{ selectedMail()!.adminDisplayName }}</p>
            <p><strong>An:</strong> {{ selectedMail()!.recipientDisplayName }} ({{ selectedMail()!.recipientEmail }})</p>
            <p><strong>Betreff:</strong> {{ selectedMail()!.subject }}</p>
            <p><strong>Gesendet:</strong> {{ selectedMail()!.sentAt | date:'dd.MM.yyyy HH:mm' }}</p>
            @if (selectedMail()!.hasDeadline) {
              <p><strong>Frist-Aktion:</strong> {{ selectedMail()!.deadlineAction }}</p>
            }
            <hr />
            <div class="mail-body">{{ selectedMail()!.body }}</div>
          </div>
        }
      </p-dialog>
    </div>
  `,
  styles: [`
    .admin-mails { padding: 2rem; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .mail-detail { display: flex; flex-direction: column; gap: 0.5rem; }
    .mail-body { white-space: pre-wrap; background: #f5f5f5; padding: 1rem; border-radius: 4px; }
  `]
})
export class AdminMailsComponent implements OnInit {
  mails = signal<AdminMail[]>([]);
  totalRecords = signal(0);
  currentPage = 0;
  showDetail = false;
  selectedMail = signal<AdminMail | null>(null);

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadMails();
  }

  loadMails() {
    this.adminService.getMails(this.currentPage, 20).subscribe(page => {
      this.mails.set(page.content);
      this.totalRecords.set(page.totalElements);
    });
  }

  onPageChange(event: any) {
    this.currentPage = event.first / event.rows;
    this.loadMails();
  }

  showMailDetail(mail: AdminMail) {
    this.selectedMail.set(mail);
    this.showDetail = true;
  }
}
