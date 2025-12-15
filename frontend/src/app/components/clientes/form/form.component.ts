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
  clienteId?: number;

  constructor(
    private clienteService: ClienteService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.clienteId = +id;
      this.carregarCliente(this.clienteId);
    }
  }

  carregarCliente(id: number): void {
    this.loading = true;
    this.clienteService.buscarPorId(id).subscribe({
      next: (cliente) => {
        this.cliente = cliente;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar cliente';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';

    if (this.isEditMode && this.clienteId) {
      this.clienteService.atualizar(this.clienteId, this.cliente).subscribe({
        next: () => this.router.navigate(['/clientes']),
        error: () => {
          this.errorMessage = 'Erro ao atualizar cliente';
          this.loading = false;
        }
      });
    } else {
      this.clienteService.criar(this.cliente).subscribe({
        next: () => this.router.navigate(['/clientes']),
        error: () => {
          this.errorMessage = 'Erro ao criar cliente';
          this.loading = false;
        }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/clientes']);
  }
}
