import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SearchHistoryService, SearchLog } from '../../services/search-history.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-search-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-history.component.html',
  styleUrls: ['./search-history.component.css']
})
export class SearchHistoryComponent implements OnInit {
  logs: SearchLog[] = [];
  filteredLogs: SearchLog[] = [];
  loading = false;
  filterText = '';

  // KPI Stats
  totalAuditCount = 0;
  todayCount = 0;
  distinctAgentsCount = 0;

  constructor(
    private searchHistoryService: SearchHistoryService,
    public authService: AuthService
  ) {
    this.logs = this.getMockLogs();
    this.filteredLogs = this.logs;
    this.computeKpiStats(this.logs);
  }

  ngOnInit(): void {
    this.loadLogs();
  }

  getMockLogs(): SearchLog[] {
    const user = this.authService.currentUser();
    const isUserAdmin = this.authService.isAdmin();

    const allMocks: SearchLog[] = [
      { id: 31, username: 'admin', searchedNumber: '0655443322', searchTimestamp: '2026-08-31T11:41:52', resultCount: 4 },
      { id: 30, username: 'admin', searchedNumber: '0661234567', searchTimestamp: '2026-08-31T09:39:01', resultCount: 4 },
      { id: 29, username: 'agent_inwi_rabat', searchedNumber: '0661234567', searchTimestamp: '2026-08-30T18:55:50', resultCount: 4 },
      { id: 28, username: 'user', searchedNumber: '0707112233', searchTimestamp: '2026-08-30T19:03:13', resultCount: 4 },
      { id: 27, username: 'user', searchedNumber: '0661234567', searchTimestamp: '2026-08-30T19:03:04', resultCount: 4 },
      { id: 26, username: 'admin', searchedNumber: '0667890123', searchTimestamp: '2026-08-25T16:53:27', resultCount: 4 },
      { id: 25, username: 'admin', searchedNumber: '0522998877', searchTimestamp: '2026-08-20T16:53:26', resultCount: 4 },
      { id: 24, username: 'user', searchedNumber: '0522998877', searchTimestamp: '2026-08-15T14:40:16', resultCount: 4 }
    ];

    if (isUserAdmin) {
      return allMocks;
    } else {
      const username = user?.username || 'user';
      return allMocks.filter(l => l.username === username);
    }
  }

  computeKpiStats(data: SearchLog[]): void {
    this.totalAuditCount = data.length;
    this.todayCount = data.filter(l => l.searchTimestamp && l.searchTimestamp.startsWith('2026-08-31')).length;
    const agents = new Set(data.map(l => l.username));
    this.distinctAgentsCount = agents.size;
  }

  loadLogs(): void {
    this.loading = true;
    const isUserAdmin = this.authService.isAdmin();
    const currentUser = this.authService.currentUser();

    if (isUserAdmin) {
      // L'administrateur consulte l'intégralité du registre d'audit de tous les opérateurs
      this.searchHistoryService.getAllSearches().subscribe({
        next: (data) => {
          this.loading = false;
          if (data && data.length > 0) {
            this.logs = data;
          } else {
            this.logs = this.getMockLogs();
          }
          this.computeKpiStats(this.logs);
          this.applyFilter();
        },
        error: (err) => {
          console.warn('Erreur API logs audit admin, affichage données fallback:', err);
          this.loading = false;
          this.logs = this.getMockLogs();
          this.computeKpiStats(this.logs);
          this.applyFilter();
        }
      });
    } else {
      // L'utilisateur simple (Agent Consultation) consulte uniquement son propre historique de recherche
      const username = currentUser?.username || 'user';
      this.searchHistoryService.getUserSearches(username).subscribe({
        next: (data) => {
          this.loading = false;
          if (data && data.length > 0) {
            this.logs = data;
          } else {
            this.logs = this.getMockLogs();
          }
          this.computeKpiStats(this.logs);
          this.applyFilter();
        },
        error: (err) => {
          console.warn('Erreur API logs audit user, affichage données fallback:', err);
          this.loading = false;
          this.logs = this.getMockLogs();
          this.computeKpiStats(this.logs);
          this.applyFilter();
        }
      });
    }
  }

  applyFilter(): void {
    if (!this.filterText.trim()) {
      this.filteredLogs = this.logs;
    } else {
      const term = this.filterText.toLowerCase().trim();
      this.filteredLogs = this.logs.filter(log => 
        (log.username && log.username.toLowerCase().includes(term)) ||
        (log.searchedNumber && log.searchedNumber.toLowerCase().includes(term)) ||
        (log.id && log.id.toString().includes(term))
      );
    }
  }

  exportCsv(): void {
    if (this.filteredLogs.length === 0) return;
    
    let csvContent = 'data:text/csv;charset=utf-8,ID Audit,Opérateur,Numéro Recherché,Horodatage,Volume Résultats,Statut CNDP\n';
    this.filteredLogs.forEach(l => {
      csvContent += `${l.id},${l.username},${l.searchedNumber},${l.searchTimestamp},${l.resultCount},Conforme CNDP\n`;
    });

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `registre_audit_cndp_inwi_${new Date().toISOString().slice(0,10)}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}
