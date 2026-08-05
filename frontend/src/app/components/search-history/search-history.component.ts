import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchHistoryService, SearchLog } from '../../services/search-history.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-search-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './search-history.component.html',
  styleUrls: ['./search-history.component.css']
})
export class SearchHistoryComponent implements OnInit {
  logs: SearchLog[] = [];
  loading = false;

  constructor(
    private searchHistoryService: SearchHistoryService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs(): void {
    this.loading = true;
    const user = this.authService.currentUser();
    
    if (this.authService.isAdmin()) {
      this.searchHistoryService.getAllSearches().subscribe({
        next: (data) => {
          this.logs = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Erreur chargement traçabilité:', err);
          this.loading = false;
        }
      });
    } else if (user?.username) {
      this.searchHistoryService.getUserSearches(user.username).subscribe({
        next: (data) => {
          this.logs = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Erreur chargement traçabilité utilisateur:', err);
          this.loading = false;
        }
      });
    }
  }
}
