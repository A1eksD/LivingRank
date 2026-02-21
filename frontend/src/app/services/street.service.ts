import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Street, StreetDetail } from '../models/street.model';

@Injectable({ providedIn: 'root' })
export class StreetService {

  constructor(private http: HttpClient) {}

  searchStreets(query: string): Observable<Street[]> {
    return this.http.get<Street[]>(`${environment.apiUrl}/streets/search`, {
      params: { q: query }
    });
  }

  getStreetDetail(id: number): Observable<StreetDetail> {
    return this.http.get<StreetDetail>(`${environment.apiUrl}/streets/${id}`);
  }
}
