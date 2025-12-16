import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteService } from '../../../services/cliente.service';
import { Cliente } from '../../../models/cliente.model';

@Component({
  selector: 'app-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './form.component.html',
  styleUrl: './form.component.scss'
})
export class FormComponent implements OnInit {
  cliente: Cliente = {
    nome: '',
    cpf: '',
    email: '',
    senha: '',
    telefone: '',
    dataNascimento: ''
  };
  isEditMode = false;
  loading = false;
  errorMessage = '';
  clienteId?: string;

  constructor(
    private clienteService: ClienteService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.clienteId = id;
      this.carregarCliente(this.clienteId);
    }
  }

  carregarCliente(id: string): void {
    this.loading = true;
    this.clienteService.buscarPorId(id).subscribe({
      next: (cliente) => {
        console.log('Cliente carregado:', cliente);
        // Formata o telefone de volta para formato visual
        this.cliente = {
          ...cliente,
          telefone: this.formatarTelefoneParaExibicao(cliente.telefone)
        };
        this.loading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar cliente:', error);
        this.errorMessage = 'Erro ao carregar cliente';
        this.loading = false;
      }
    });
  }

  formatarTelefoneParaExibicao(telefone: string): string {
    if (!telefone) return '';

    // Remove tudo exceto números
    const numeros = telefone.replace(/\D/g, '');

    // Se começa com 55 (código do Brasil), remove
    const somenteNumero = numeros.startsWith('55') ? numeros.substring(2) : numeros;

    // Formata: 11 96888-2222
    if (somenteNumero.length === 11) {
      return `${somenteNumero.substring(0, 2)} ${somenteNumero.substring(2, 7)}-${somenteNumero.substring(7)}`;
    }

    // Retorna o número sem formatação se não conseguir formatar
    return somenteNumero;
  }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';

    // Preparar dados para envio ao backend
    const clienteData: any = {
      ...this.cliente
      // CPF e telefone já estão formatados corretamente, mantém como está
    };

    // Remove senha ao editar (não é permitido atualizar senha)
    if (this.isEditMode) {
      delete clienteData.senha;
    }

    console.log('Enviando dados para o backend:', clienteData);

    if (this.isEditMode && this.clienteId) {
      this.clienteService.atualizar(this.clienteId, clienteData).subscribe({
        next: () => this.router.navigate(['/clientes']),
        error: (error) => {
          console.error('Erro ao atualizar:', error);
          this.errorMessage = error.error?.message || 'Erro ao atualizar cliente';
          this.loading = false;
        }
      });
    } else {
      this.clienteService.criar(clienteData).subscribe({
        next: () => this.router.navigate(['/clientes']),
        error: (error) => {
          console.error('Erro ao criar:', error);
          this.errorMessage = error.error?.message || 'Erro ao criar cliente';
          this.loading = false;
        }
      });
    }
  }

  formatTelefoneParaBackend(telefone: string): string {
    // Remove todos os caracteres não numéricos
    const numbersOnly = telefone.replace(/\D/g, '');
    // Adiciona o código do país (+55) para o formato E.164
    return `+55${numbersOnly}`;
  }

  cancelar(): void {
    this.router.navigate(['/clientes']);
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

    this.cliente.cpf = value;
  }

  formatTelefone(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '');

    if (value.length > 11) {
      value = value.substring(0, 11);
    }

    if (value.length > 6) {
      value = value.replace(/(\d{2})(\d{5})(\d{1,4})/, '$1 $2-$3');
    } else if (value.length > 2) {
      value = value.replace(/(\d{2})(\d{1,5})/, '$1 $2');
    }

    this.cliente.telefone = value;
  }
}
