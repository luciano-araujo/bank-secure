import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthRequest } from '../../models/auth.model';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  credentials: AuthRequest = {
    email: '',
    senha: ''
  };
  errorMessage = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onSubmit(): void {
    if (!this.credentials.email || !this.credentials.senha) {
      this.errorMessage = 'Email e senha são obrigatórios';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        this.loading = false;
        if (response.authenticated) {
          this.router.navigate(['/home']);
        } else {
          this.errorMessage = 'Credenciais inválidas';
        }
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Erro ao realizar login. Tente novamente.';
      }
    });
  }

  acessarComoAnonimo(): void {
    this.router.navigate(['/home']);
  }
}
