import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FuncionarioService } from '../../../services/funcionario.service';
import { Funcionario } from '../../../models/funcionario.model';

@Component({
  selector: 'app-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './form.component.html',
  styleUrl: './form.component.scss'
})
export class FormComponent implements OnInit {
  funcionario: Funcionario = {
    nome: '',
    cpf: '',
    email: '',
    senha: '',
    cargo: '',
    dataAdmissao: ''
  };
  isEditMode = false;
  loading = false;
  errorMessage = '';
  funcionarioId?: number;

  constructor(
    private funcionarioService: FuncionarioService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.funcionarioId = +id;
      this.carregarFuncionario(this.funcionarioId);
    }
  }

  carregarFuncionario(id: number): void {
    this.loading = true;
    this.funcionarioService.buscarPorId(id).subscribe({
      next: (funcionario) => {
        this.funcionario = funcionario;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar funcionário';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';

    if (this.isEditMode && this.funcionarioId) {
      this.funcionarioService.atualizar(this.funcionarioId, this.funcionario).subscribe({
        next: () => this.router.navigate(['/funcionarios']),
        error: () => {
          this.errorMessage = 'Erro ao atualizar funcionário';
          this.loading = false;
        }
      });
    } else {
      this.funcionarioService.criar(this.funcionario).subscribe({
        next: () => this.router.navigate(['/funcionarios']),
        error: () => {
          this.errorMessage = 'Erro ao criar funcionário';
          this.loading = false;
        }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/funcionarios']);
  }
}
