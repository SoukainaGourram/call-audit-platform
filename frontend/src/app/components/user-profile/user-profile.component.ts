import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
  username = '';
  fullName = '';
  email = '';
  role = '';

  oldPassword = '';
  newPassword = '';
  confirmPassword = '';

  profileMsg = '';
  profileError = '';

  passwordMsg = '';
  passwordError = '';

  constructor(
    private userService: UserService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUser();
    if (user) {
      this.username = user.username;
      this.fullName = user.fullName || user.username;
      this.email = user.email || user.username + '@inwi.ma';
      this.role = user.role || 'USER';
    }
  }

  onUpdateProfile(): void {
    this.profileMsg = '';
    this.profileError = '';

    this.userService.updateProfile(this.username, this.fullName, this.email).subscribe({
      next: (res) => {
        this.profileMsg = 'Profil mis à jour avec succès !';
        this.authService.updateStoredUserInfo({ fullName: res.fullName, email: res.email });
        setTimeout(() => (this.profileMsg = ''), 4000);
      },
      error: (err) => {
        this.profileError = err.error?.message || 'Erreur lors de la mise à jour du profil.';
      }
    });
  }

  onChangePassword(): void {
    this.passwordMsg = '';
    this.passwordError = '';

    if (!this.oldPassword || !this.newPassword) {
      this.passwordError = 'Veuillez saisir l\'ancien et le nouveau mot de passe.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.passwordError = 'Le nouveau mot de passe et sa confirmation ne correspondent pas.';
      return;
    }

    this.userService.changePassword(this.username, this.oldPassword, this.newPassword).subscribe({
      next: () => {
        this.passwordMsg = 'Mot de passe modifié avec succès !';
        this.oldPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
        setTimeout(() => (this.passwordMsg = ''), 4000);
      },
      error: (err) => {
        this.passwordError = err.error?.message || 'Erreur lors de la modification du mot de passe.';
      }
    });
  }
}
