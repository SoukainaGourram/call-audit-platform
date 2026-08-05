import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CallHistoryRecord {
  id: number;
  caller: string;
  callee: string;
  startedAt: string;
  endedAt: string;
  duration: string;
  status: string;
  type: string;
  location: string;
}

@Injectable({
  providedIn: 'root'
})
export class CallHistoryService {
  private apiUrl = environment.callApiUrl;

  constructor(private http: HttpClient) {}

  getAllCalls(): Observable<CallHistoryRecord[]> {
    return this.http.get<CallHistoryRecord[]>(this.apiUrl);
  }

  searchCalls(number: string, username: string): Observable<CallHistoryRecord[]> {
    let params = new HttpParams().set('number', number).set('username', username);
    return this.http.get<CallHistoryRecord[]>(`${this.apiUrl}/search`, { params });
  }
}
