import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { DropdownModule } from 'primeng/dropdown';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AdminService } from '../../../services/admin.service';
import { AdminReview } from '../../../models/admin.model';

@Component({
  selector: 'app-admin-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, TableModule, ButtonModule, TagModule,
    DropdownModule, ConfirmDialogModule, ToastModule],
  providers: [ConfirmationService, MessageService],
  template: `
    <div class="admin-reviews">
      <div class="header">
        <h1>Review-Verwaltung</h1>
        <a routerLink="/admin"><button pButton label="Zurueck" icon="pi pi-arrow-left" class="p-button-text"></button></a>
      </div>

      <div class="filters">
        <p-dropdown [options]="visibilityOptions" [(ngModel)]="visibilityFilter" placeholder="Sichtbarkeit"
                    (onChange)="loadReviews()" [showClear]="true"></p-dropdown>
      </div>

      <p-table [value]="reviews()" [paginator]="true" [rows]="20" [totalRecords]="totalRecords()"
               [lazy]="true" (onLazyLoad)="onPageChange($event)">
        <ng-template pTemplate="header">
          <tr>
            <th>ID</th>
            <th>Strasse / Stadt</th>
            <th>Benutzer</th>
            <th>Bewertung</th>
            <th>Kommentar</th>
            <th>Sichtbar</th>
            <th>Erstellt</th>
            <th>Aktionen</th>
          </tr>
        </ng-template>
        <ng-template pTemplate="body" let-review>
          <tr>
            <td>{{ review.id }}</td>
            <td>{{ review.streetName }}, {{ review.streetCity }}</td>
            <td>{{ review.userDisplayName }}<br/><small>{{ review.userEmail }}</small></td>
            <td>{{ review.overallRating }}/5</td>
            <td class="comment-cell">{{ review.comment | slice:0:80 }}{{ review.comment && review.comment.length > 80 ? '...' : '' }}</td>
            <td>
              <p-tag [value]="review.visible ? 'Sichtbar' : 'Ausgeblendet'"
                     [severity]="review.visible ? 'success' : 'warn'"></p-tag>
            </td>
            <td>{{ review.createdAt | date:'dd.MM.yyyy' }}</td>
            <td class="actions">
              @if (review.visible) {
                <button pButton icon="pi pi-eye-slash" class="p-button-warning p-button-sm"
                        pTooltip="Ausblenden" (click)="hideReview(review)"></button>
              } @else {
                <button pButton icon="pi pi-eye" class="p-button-success p-button-sm"
                        pTooltip="Einblenden" (click)="showReview(review)"></button>
              }
              <button pButton icon="pi pi-trash" class="p-button-danger p-button-sm"
                      pTooltip="Loeschen" (click)="confirmDelete(review)"></button>
            </td>
          </tr>
        </ng-template>
      </p-table>

      <p-confirmDialog></p-confirmDialog>
      <p-toast></p-toast>
    </div>
  `,
  styles: [`
    .admin-reviews { padding: 2rem; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .filters { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
    .actions { display: flex; gap: 0.25rem; }
    .comment-cell { max-width: 200px; word-break: break-word; }
  `]
})
export class AdminReviewsComponent implements OnInit {
  reviews = signal<AdminReview[]>([]);
  totalRecords = signal(0);
  currentPage = 0;
  visibilityFilter: boolean | null = null;

  visibilityOptions = [
    { label: 'Sichtbar', value: true },
    { label: 'Ausgeblendet', value: false }
  ];

  constructor(
    private adminService: AdminService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.loadReviews();
  }

  loadReviews() {
    this.adminService.getReviews(this.currentPage, 20, undefined, undefined, this.visibilityFilter ?? undefined)
      .subscribe(page => {
        this.reviews.set(page.content);
        this.totalRecords.set(page.totalElements);
      });
  }

  onPageChange(event: any) {
    this.currentPage = event.first / event.rows;
    this.loadReviews();
  }

  hideReview(review: AdminReview) {
    this.adminService.hideReview(review.id).subscribe({
      next: res => {
        this.messageService.add({ severity: 'success', summary: 'Erfolg', detail: res.message });
        this.loadReviews();
      },
      error: err => this.messageService.add({ severity: 'error', summary: 'Fehler', detail: err.error?.message || 'Fehler' })
    });
  }

  showReview(review: AdminReview) {
    this.adminService.showReview(review.id).subscribe({
      next: res => {
        this.messageService.add({ severity: 'success', summary: 'Erfolg', detail: res.message });
        this.loadReviews();
      },
      error: err => this.messageService.add({ severity: 'error', summary: 'Fehler', detail: err.error?.message || 'Fehler' })
    });
  }

  confirmDelete(review: AdminReview) {
    this.confirmationService.confirm({
      message: `Review #${review.id} wirklich loeschen? Dies kann nicht rueckgaengig gemacht werden.`,
      header: 'Review loeschen',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.adminService.deleteReview(review.id).subscribe({
          next: res => {
            this.messageService.add({ severity: 'success', summary: 'Erfolg', detail: res.message });
            this.loadReviews();
          },
          error: err => this.messageService.add({ severity: 'error', summary: 'Fehler', detail: err.error?.message || 'Fehler' })
        });
      }
    });
  }
}
