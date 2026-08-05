import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  errorMsg = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onLogin(): void {
    if (!this.username || !this.password) {
      this.errorMsg = 'Veuillez saisir votre nom d\'utilisateur et mot de passe';
      return;
    }
    this.loading = true;
    this.errorMsg = '';

    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/search']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMsg = err.error?.message || err.error || 'Échec d\'authentification. Vérifiez vos identifiants.';
      }
    });
  }

  quickLogin(role: 'admin' | 'user'): void {
    if (role === 'admin') {
      this.username = 'admin';
      this.password = 'admin123';
    } else {
      this.username = 'user';
      this.password = 'user123';
    }
    this.onLogin();
  }
}
