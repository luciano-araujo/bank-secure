import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FuncionarioService } from '../../../services/funcionario.service';
import { Funcionario } from '../../../models/funcionario.model';

@Component({
  selector: 'app-lista',
  imports: [CommonModule],
  templateUrl: './lista.component.html',
  styleUrl: './lista.component.scss'
})
export class ListaComponent implements OnInit {
  funcionarios: Funcionario[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private funcionarioService: FuncionarioService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.carregarFuncionarios();
  }

  carregarFuncionarios(): void {
    this.loading = true;
    this.funcionarioService.listar().subscribe({
      next: (funcionarios) => {
        this.funcionarios = funcionarios;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar funcionários';
        this.loading = false;
      }
    });
  }

  editar(id: number): void {
    this.router.navigate(['/funcionarios/editar', id]);
  }

  deletar(id: number): void {
    if (confirm('Deseja realmente excluir este funcionário?')) {
      this.funcionarioService.deletar(id).subscribe({
        next: () => this.carregarFuncionarios(),
        error: () => this.errorMessage = 'Erro ao deletar funcionário'
      });
    }
  }

  voltar(): void {
    this.router.navigate(['/home']);
  }

  novoFuncionario(): void {
    this.router.navigate(['/funcionarios/novo']);
  }
}
