import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { RatingModule } from 'primeng/rating';
import { FormsModule } from '@angular/forms';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { StreetService } from '../../services/street.service';
import { Street } from '../../models/street.model';

@Component({
  selector: 'app-search-results',
  imports: [CommonModule, RouterLink, CardModule, RatingModule, FormsModule, ProgressSpinnerModule],
  templateUrl: './search-results.component.html',
  styleUrl: './search-results.component.scss'
})
export class SearchResultsComponent implements OnInit {
  streets: Street[] = [];
  query = '';
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private streetService: StreetService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.query = params['q'] || '';
      if (this.query) {
        this.search();
      }
    });
  }

  search(): void {
    this.loading = true;
    this.streetService.searchStreets(this.query).subscribe({
      next: (streets) => {
        this.streets = streets;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  getRating(street: Street): number {
    return street.averageRating ? Math.round(street.averageRating) : 0;
  }
}
