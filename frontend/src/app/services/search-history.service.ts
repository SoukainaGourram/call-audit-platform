import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface SearchLog {
  id: number;
  username: string;
  searchedNumber: string;
  searchTimestamp: string;
  resultCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class SearchHistoryService {
  private apiUrl = environment.searchApiUrl;

  constructor(private http: HttpClient) {}

  getAllSearches(): Observable<SearchLog[]> {
    return this.http.get<SearchLog[]>(this.apiUrl);
  }

  getUserSearches(username: string): Observable<SearchLog[]> {
    return this.http.get<SearchLog[]>(`${this.apiUrl}/user/${username}`);
  }
}
