import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CallHistoryService, CallHistoryRecord } from '../../services/call-history.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-call-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './call-search.component.html',
  styleUrls: ['./call-search.component.css']
})
export class CallSearchComponent implements OnInit {
  searchQuery = '';
  calls: CallHistoryRecord[] = [];
  loading = false;
  hasSearched = false;
  loggedSearchAlert = false;
  lastSearchedNumber = '';

  constructor(
    private callHistoryService: CallHistoryService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAllCalls();
  }

  loadAllCalls(): void {
    this.loading = true;
    this.callHistoryService.getAllCalls().subscribe({
      next: (data) => {
        this.calls = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement appels:', err);
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    if (!this.searchQuery.trim()) {
      this.loadAllCalls();
      this.hasSearched = false;
      return;
    }

    this.loading = true;
    this.hasSearched = true;
    this.lastSearchedNumber = this.searchQuery.trim();
    const currentUser = this.authService.currentUser()?.username || 'user';

    this.callHistoryService.searchCalls(this.lastSearchedNumber, currentUser).subscribe({
      next: (results) => {
        this.calls = results;
        this.loading = false;
        this.loggedSearchAlert = true;
        setTimeout(() => (this.loggedSearchAlert = false), 4000);
      },
      error: (err) => {
        console.error('Erreur recherche:', err);
        this.loading = false;
      }
    });
  }

  quickSearch(num: string): void {
    this.searchQuery = num;
    this.onSearch();
  }

  resetSearch(): void {
    this.searchQuery = '';
    this.hasSearched = false;
    this.loadAllCalls();
  }
}
