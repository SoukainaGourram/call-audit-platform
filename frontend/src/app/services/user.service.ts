import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UserProfile {
  id?: number;
  username: string;
  password?: string;
  fullName: string;
  email: string;
  role: string;
  enabled: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = environment.userApiUrl;

  constructor(private http: HttpClient) {}

  getUsers(): Observable<UserProfile[]> {
    return this.http.get<UserProfile[]>(this.apiUrl);
  }

  getUserById(id: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/${id}`);
  }

  createUser(user: UserProfile): Observable<UserProfile> {
    return this.http.post<UserProfile>(this.apiUrl, user);
  }

  updateUser(id: number, user: UserProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/${id}`, user);
  }

  deleteUser(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }

  toggleStatus(id: number): Observable<UserProfile> {
    return this.http.patch<UserProfile>(`${this.apiUrl}/${id}/toggle-status`, {});
  }

  updateProfile(username: string, fullName: string, email: string): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/profile/${username}`, { fullName, email });
  }

  changePassword(username: string, oldPassword: string, newPassword: string): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.apiUrl}/profile/${username}/change-password`, { oldPassword, newPassword });
  }
}
