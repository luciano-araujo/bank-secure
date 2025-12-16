import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteService } from '../../../services/cliente.service';
import { Cliente } from '../../../models/cliente.model';

@Component({
  selector: 'app-lista',
  imports: [CommonModule],
  templateUrl: './lista.component.html',
  styleUrl: './lista.component.scss'
})
export class ListaComponent implements OnInit {
  clientes: Cliente[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private clienteService: ClienteService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.carregarClientes();
  }

  carregarClientes(): void {
    this.loading = true;
    this.clienteService.listar().subscribe({
      next: (clientes) => {
        console.log('Clientes recebidos do backend:', clientes);
        this.clientes = clientes;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar clientes';
        this.loading = false;
      }
    });
  }

  editar(id: string): void {
    console.log('Editando cliente com ID:', id);
    if (!id) {
      this.errorMessage = 'ID do cliente inválido';
      return;
    }
    this.router.navigate(['/clientes/editar', id]);
  }

  deletar(id: string): void {
    if (confirm('Deseja realmente excluir este cliente?')) {
      this.clienteService.deletar(id).subscribe({
        next: () => this.carregarClientes(),
        error: () => this.errorMessage = 'Erro ao deletar cliente'
      });
    }
  }

  voltar(): void {
    this.router.navigate(['/home']);
  }

  novoCliente(): void {
    this.router.navigate(['/clientes/novo']);
  }

  formatarTelefone(telefone: string): string {
    if (!telefone) return '';

    // Remove tudo exceto números
    const numeros = telefone.replace(/\D/g, '');

    // Se começa com 55 (código do Brasil), remove
    const somenteNumero = numeros.startsWith('55') ? numeros.substring(2) : numeros;

    // Formata: 11 96888-2222
    if (somenteNumero.length === 11) {
      return `${somenteNumero.substring(0, 2)} ${somenteNumero.substring(2, 7)}-${somenteNumero.substring(7)}`;
    }

    // Retorna o original se não conseguir formatar
    return telefone;
  }
}
