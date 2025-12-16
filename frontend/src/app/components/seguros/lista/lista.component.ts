import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SeguroService } from '../../../services/seguro.service';
import { AuthService } from '../../../services/auth.service';
import { Seguro } from '../../../models/seguro.model';

@Component({
  selector: 'app-lista',
  imports: [CommonModule],
  templateUrl: './lista.component.html',
  styleUrl: './lista.component.scss'
})
export class ListaComponent implements OnInit {
  seguros: Seguro[] = [];
  loading = false;
  errorMessage = '';
  isFuncionario = false;

  constructor(
    private seguroService: SeguroService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.isFuncionario = this.authService.isFuncionario();
    this.carregarSeguros();
  }

  carregarSeguros(): void {
    this.loading = true;
    this.errorMessage = '';

    this.seguroService.listar().subscribe({
      next: (seguros) => {
        this.seguros = seguros;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar seguros';
        this.loading = false;
      }
    });
  }

  editar(id: string): void {
    console.log('Editando seguro com ID:', id);
    this.router.navigate(['/seguros/editar', id]);
  }

  deletar(id: string): void {
    if (confirm('Deseja realmente excluir este seguro?')) {
      this.seguroService.deletar(id).subscribe({
        next: () => {
          this.carregarSeguros();
        },
        error: () => {
          this.errorMessage = 'Erro ao deletar seguro';
        }
      });
    }
  }

  voltar(): void {
    this.router.navigate(['/home']);
  }

  novoSeguro(): void {
    this.router.navigate(['/seguros/novo']);
  }
}
