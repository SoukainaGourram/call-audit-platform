import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CallHistoryService, CallHistoryRecord } from '../../services/call-history.service';
import { AuthService } from '../../services/auth.service';

export interface FilterState {
  number: string;
  dateRange: 'ALL' | 'TODAY' | 'YESTERDAY' | 'MONTH';
  type: 'ALL' | 'INCOMING' | 'OUTGOING' | 'MISSED';
}

@Component({
  selector: 'app-call-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './call-search.component.html',
  styleUrls: ['./call-search.component.css']
})
export class CallSearchComponent implements OnInit {
  // Objet d'état partagé regroupant l'ensemble des filtres actifs
  filterState: FilterState = {
    number: '0661234567',
    dateRange: 'ALL',
    type: 'ALL'
  };

  searchQuery = '0661234567';
  allCalls: CallHistoryRecord[] = [];
  filteredCalls: CallHistoryRecord[] = [];
  paginatedCalls: CallHistoryRecord[] = [];
  loading = false;
  hasSearched = false;
  loggedSearchAlert = false;
  lastSearchedNumber = '0661234567';

  // KPI Stats
  totalCount = 0;
  totalDurationStr = '00:00:00';
  successCount = 0;
  missedCount = 0;

  // Tri des colonnes
  sortField: 'startedAt' | 'duration' | null = 'startedAt';
  sortDirection: 'asc' | 'desc' = 'desc';

  // Pagination
  currentPage = 1;
  pageSize = 10;
  totalPages = 1;

  // Modale de détail d'un appel
  selectedCallDetail: CallHistoryRecord | null = null;
  showDetailModal = false;

  constructor(
    private callHistoryService: CallHistoryService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.executeCombinedSearch();
  }

  onSearch(): void {
    this.filterState.number = this.searchQuery.trim();
    this.executeCombinedSearch();
  }

  setDateFilter(range: 'ALL' | 'TODAY' | 'YESTERDAY' | 'MONTH'): void {
    this.filterState.dateRange = range;
    this.executeCombinedSearch();
  }

  setTypeFilter(type: 'ALL' | 'INCOMING' | 'OUTGOING' | 'MISSED'): void {
    this.filterState.type = type;
    this.executeCombinedSearch();
  }

  quickSearch(num: string): void {
    this.searchQuery = num;
    this.filterState.number = num;
    this.executeCombinedSearch();
  }

  resetSearch(): void {
    this.searchQuery = '0661234567';
    this.filterState = {
      number: '0661234567',
      dateRange: 'ALL',
      type: 'ALL'
    };
    this.executeCombinedSearch();
  }

  /**
   * Déclenche UNE SEULE requête combinée vers le backend avec l'ensemble des critères actifs
   */
  executeCombinedSearch(): void {
    this.loading = true;
    this.hasSearched = true;
    this.lastSearchedNumber = this.filterState.number || 'Tous';

    const currentUser = this.authService.currentUser()?.username || 'admin';

    this.callHistoryService.searchCalls(
      this.filterState.number,
      this.filterState.dateRange,
      this.filterState.type,
      currentUser
    ).subscribe({
      next: (results) => {
        this.loading = false;
        this.allCalls = results || [];
        this.applySortingAndPagination();
        this.computeKpiStats(this.allCalls);
        this.loggedSearchAlert = true;
        setTimeout(() => (this.loggedSearchAlert = false), 4000);
      },
      error: (err) => {
        console.warn('Erreur API backend:', err);
        this.loading = false;
        this.allCalls = [];
        this.applySortingAndPagination();
        this.computeKpiStats([]);
      }
    });
  }

  toggleSort(field: 'startedAt' | 'duration'): void {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'desc';
    }
    this.applySortingAndPagination();
  }

  applySortingAndPagination(): void {
    let sorted = [...this.allCalls];

    if (this.sortField) {
      sorted.sort((a, b) => {
        let valA = a[this.sortField!] || '';
        let valB = b[this.sortField!] || '';
        let res = valA.localeCompare(valB);
        return this.sortDirection === 'asc' ? res : -res;
      });
    }

    this.filteredCalls = sorted;
    this.totalPages = Math.ceil(this.filteredCalls.length / this.pageSize) || 1;
    this.currentPage = Math.min(this.currentPage, this.totalPages);
    if (this.currentPage < 1) this.currentPage = 1;

    const startIdx = (this.currentPage - 1) * this.pageSize;
    this.paginatedCalls = this.filteredCalls.slice(startIdx, startIdx + this.pageSize);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      const startIdx = (this.currentPage - 1) * this.pageSize;
      this.paginatedCalls = this.filteredCalls.slice(startIdx, startIdx + this.pageSize);
    }
  }

  openDetailModal(call: CallHistoryRecord): void {
    this.selectedCallDetail = call;
    this.showDetailModal = true;
  }

  closeDetailModal(): void {
    this.showDetailModal = false;
    this.selectedCallDetail = null;
  }

  computeKpiStats(data: CallHistoryRecord[]): void {
    this.totalCount = data.length;
    this.missedCount = data.filter(c => c.type === 'MISSED' || c.status === 'MANQUE' || c.status === 'Manqué').length;
    this.successCount = data.filter(c => c.status === 'TERMINE' || c.status === 'Réussi').length;

    if (data.length > 0) {
      let totalSecs = 0;
      data.forEach(c => {
        if (c.duration) {
          const parts = c.duration.split(/[:hm\s]/).filter(Boolean);
          if (parts.length === 2) {
            totalSecs += parseInt(parts[0]) * 60 + parseInt(parts[1]);
          } else if (parts.length === 3) {
            totalSecs += parseInt(parts[0]) * 3600 + parseInt(parts[1]) * 60 + parseInt(parts[2]);
          }
        }
      });

      if (totalSecs > 0) {
        const hrs = Math.floor(totalSecs / 3600);
        const mins = Math.floor((totalSecs % 3600) / 60);
        const secs = totalSecs % 60;
        this.totalDurationStr = `${hrs < 10 ? '0' + hrs : hrs}:${mins < 10 ? '0' + mins : mins}:${secs < 10 ? '0' + secs : secs}`;
      } else {
        this.totalDurationStr = '00:00:00';
      }
    } else {
      this.totalDurationStr = '00:00:00';
    }
  }
}
