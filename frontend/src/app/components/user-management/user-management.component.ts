import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, UserProfile } from '../../services/user.service';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: UserProfile[] = [];
  loading = false;
  showModal = false;
  editMode = false;
  errorMsg = '';
  successMsg = '';

  currentUserForm: UserProfile = {
    username: '',
    password: '',
    fullName: '',
    email: '',
    role: 'USER',
    enabled: true
  };

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement utilisateurs:', err);
        this.loading = false;
      }
    });
  }

  openAddModal(): void {
    this.editMode = false;
    this.errorMsg = '';
    this.currentUserForm = {
      username: '',
      password: '',
      fullName: '',
      email: '',
      role: 'USER',
      enabled: true
    };
    this.showModal = true;
  }

  openEditModal(user: UserProfile): void {
    this.editMode = true;
    this.errorMsg = '';
    this.currentUserForm = { ...user, password: '' };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  saveUser(): void {
    if (!this.currentUserForm.username || !this.currentUserForm.fullName || !this.currentUserForm.email) {
      this.errorMsg = 'Veuillez remplir tous les champs obligatoires.';
      return;
    }
    if (!this.editMode && !this.currentUserForm.password) {
      this.errorMsg = 'Le mot de passe est obligatoire pour la création.';
      return;
    }

    if (this.editMode && this.currentUserForm.id) {
      this.userService.updateUser(this.currentUserForm.id, this.currentUserForm).subscribe({
        next: () => {
          this.showSuccess('Utilisateur mis à jour avec succès');
          this.closeModal();
          this.loadUsers();
        },
        error: (err) => {
          this.errorMsg = err.error?.message || 'Erreur lors de la mise à jour.';
        }
      });
    } else {
      this.userService.createUser(this.currentUserForm).subscribe({
        next: () => {
          this.showSuccess('Utilisateur créé avec succès');
          this.closeModal();
          this.loadUsers();
        },
        error: (err) => {
          this.errorMsg = err.error?.message || 'Erreur lors de la création.';
        }
      });
    }
  }

  toggleStatus(user: UserProfile): void {
    if (user.id) {
      this.userService.toggleStatus(user.id).subscribe({
        next: (updated) => {
          user.enabled = updated.enabled;
          this.showSuccess(`Compte ${user.username} ${updated.enabled ? 'activé' : 'désactivé'}`);
        },
        error: (err) => console.error('Erreur changement statut:', err)
      });
    }
  }

  deleteUser(user: UserProfile): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer l'utilisateur "${user.username}" ?`)) {
      if (user.id) {
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            this.showSuccess('Utilisateur supprimé avec succès');
            this.loadUsers();
          },
          error: (err) => console.error('Erreur suppression:', err)
        });
      }
    }
  }

  private showSuccess(msg: string): void {
    this.successMsg = msg;
    setTimeout(() => (this.successMsg = ''), 4000);
  }
}
