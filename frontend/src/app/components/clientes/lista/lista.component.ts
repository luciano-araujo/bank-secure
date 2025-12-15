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
        this.clientes = clientes;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar clientes';
        this.loading = false;
      }
    });
  }

  editar(id: number): void {
    this.router.navigate(['/clientes/editar', id]);
  }

  deletar(id: number): void {
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
}
