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
  funcionarioId?: string;

  constructor(
    private funcionarioService: FuncionarioService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.funcionarioId = id;
      this.carregarFuncionario(this.funcionarioId);
    }
  }

  carregarFuncionario(id: string): void {
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

    // Preparar dados para envio ao backend
    const funcionarioData = {
      ...this.funcionario
      // CPF já está formatado corretamente, mantém como está
    };

    if (this.isEditMode && this.funcionarioId) {
      this.funcionarioService.atualizar(this.funcionarioId, funcionarioData).subscribe({
        next: () => this.router.navigate(['/funcionarios']),
        error: () => {
          this.errorMessage = 'Erro ao atualizar funcionário';
          this.loading = false;
        }
      });
    } else {
      this.funcionarioService.criar(funcionarioData).subscribe({
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

  formatCPF(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '');

    if (value.length > 11) {
      value = value.substring(0, 11);
    }

    if (value.length > 9) {
      value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    } else if (value.length > 6) {
      value = value.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
    } else if (value.length > 3) {
      value = value.replace(/(\d{3})(\d{1,3})/, '$1.$2');
    }

    this.funcionario.cpf = value;
  }
}
