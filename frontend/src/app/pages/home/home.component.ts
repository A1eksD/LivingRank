import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { AutoCompleteModule, AutoCompleteCompleteEvent, AutoCompleteSelectEvent } from 'primeng/autocomplete';
import { StreetService } from '../../services/street.service';
import { Street } from '../../models/street.model';

@Component({
  selector: 'app-home',
  imports: [FormsModule, InputTextModule, ButtonModule, AutoCompleteModule, CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  searchQuery = '';
  suggestions: Street[] = [];

  constructor(private streetService: StreetService, private router: Router) {}

  searchStreets(event: AutoCompleteCompleteEvent): void {
    if (event.query.length < 2) return;
    this.streetService.searchStreets(event.query).subscribe({
      next: (streets) => this.suggestions = streets
    });
  }

  onSelect(event: AutoCompleteSelectEvent): void {
    const street = event.value as Street;
    this.router.navigate(['/streets', street.id]);
  }

  onSearch(): void {
    if (this.searchQuery.length >= 2) {
      this.router.navigate(['/search'], { queryParams: { q: this.searchQuery } });
    }
  }

  displayStreet(street: Street): string {
    return `${street.streetName}, ${street.postalCode || ''} ${street.city}`;
  }
}
