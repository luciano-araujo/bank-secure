import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApoliceService } from '../../../services/apolice.service';
import { ClienteService } from '../../../services/cliente.service';
import { SeguroService } from '../../../services/seguro.service';
import { Apolice } from '../../../models/apolice.model';
import { Cliente } from '../../../models/cliente.model';
import { Seguro } from '../../../models/seguro.model';

@Component({
  selector: 'app-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './form.component.html',
  styleUrl: './form.component.scss'
})
export class FormComponent implements OnInit {
  apolice: Apolice = {
    clienteId: 0,
    seguroId: 0,
    totalCobertura: 0,
    dataInicial: '',
    dataVencimento: ''
  };
  clientes: Cliente[] = [];
  seguros: Seguro[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private apoliceService: ApoliceService,
    private clienteService: ClienteService,
    private seguroService: SeguroService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.carregarDados();
  }

  carregarDados(): void {
    this.clienteService.listar().subscribe({
      next: (clientes) => this.clientes = clientes,
      error: () => this.errorMessage = 'Erro ao carregar clientes'
    });

    this.seguroService.listar().subscribe({
      next: (seguros) => this.seguros = seguros,
      error: () => this.errorMessage = 'Erro ao carregar seguros'
    });
  }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';

    this.apoliceService.criar(this.apolice).subscribe({
      next: () => this.router.navigate(['/apolices']),
      error: () => {
        this.errorMessage = 'Erro ao criar apólice';
        this.loading = false;
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/apolices']);
  }
}
