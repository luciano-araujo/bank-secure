import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CotacaoService } from '../../services/cotacao.service';
import { ClienteService } from '../../services/cliente.service';
import { SeguroService } from '../../services/seguro.service';
import { ApoliceService } from '../../services/apolice.service';
import { Cliente } from '../../models/cliente.model';
import { Seguro } from '../../models/seguro.model';
import { CotacaoRequest, Cotacao } from '../../models/cotacao.model';

@Component({
  selector: 'app-cotacao',
  imports: [CommonModule, FormsModule],
  templateUrl: './cotacao.component.html',
  styleUrl: './cotacao.component.scss'
})
export class CotacaoComponent implements OnInit {
  clientes: Cliente[] = [];
  seguros: Seguro[] = [];
  cotacaoRequest: CotacaoRequest = {
    clienteId: 0,
    seguroId: 0
  };
  resultado?: Cotacao;
  loading = false;
  errorMessage = '';
  mostrandoFormularioVenda = false;
  vendaLoading = false;
  vendaErrorMessage = '';
  apolice = {
    totalCobertura: 0
  };

  constructor(
    private cotacaoService: CotacaoService,
    private clienteService: ClienteService,
    private seguroService: SeguroService,
    private apoliceService: ApoliceService,
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

  realizarCotacao(): void {
    this.loading = true;
    this.errorMessage = '';
    this.resultado = undefined;

    this.cotacaoService.realizar(this.cotacaoRequest).subscribe({
      next: (cotacao) => {
        this.resultado = cotacao;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao realizar cotação';
        this.loading = false;
      }
    });
  }

  voltar(): void {
    this.router.navigate(['/home']);
  }

  mostrarFormularioVenda(): void {
    this.mostrandoFormularioVenda = true;
    this.vendaErrorMessage = '';
    if (this.resultado) {
      this.apolice.totalCobertura = this.resultado.premioFinal;
    }
  }

  cancelarCotacao(): void {
    this.mostrandoFormularioVenda = false;
    this.resultado = undefined;
    this.apolice.totalCobertura = 0;
    this.vendaErrorMessage = '';
    this.errorMessage = '';
    this.cotacaoRequest = { clienteId: 0, seguroId: 0 };
  }

  getClienteNome(): string {
    const cliente = this.clientes.find(c => c.id === this.resultado?.clienteId);
    return cliente?.nome || 'Cliente não encontrado';
  }

  getSeguroTitulo(): string {
    const seguro = this.seguros.find(s => s.id === this.resultado?.seguroId);
    return seguro?.titulo || 'Seguro não encontrado';
  }

  getVigenciaTexto(): string {
    const hoje = new Date();
    const umAnoDepois = new Date();
    umAnoDepois.setFullYear(hoje.getFullYear() + 1);
    return `${hoje.toLocaleDateString('pt-BR')} até ${umAnoDepois.toLocaleDateString('pt-BR')}`;
  }

  confirmarVenda(): void {
    if (!this.resultado || !this.apolice.totalCobertura) {
      this.vendaErrorMessage = 'Informe o valor total da cobertura';
      return;
    }

    const hoje = new Date().toISOString().split('T')[0];
    const umAnoDepois = new Date();
    umAnoDepois.setFullYear(umAnoDepois.getFullYear() + 1);
    const dataVencimento = umAnoDepois.toISOString().split('T')[0];

    const apoliceData = {
      clienteId: this.resultado.clienteId,
      seguroId: this.resultado.seguroId,
      premioFinal: this.resultado.premioFinal,
      totalCobertura: this.apolice.totalCobertura,
      dataInicial: hoje,
      dataVencimento: dataVencimento
    };

    this.vendaLoading = true;
    this.apoliceService.criar(apoliceData).subscribe({
      next: () => {
        alert('✅ Venda registrada com sucesso! Apólice criada.');
        this.router.navigate(['/apolices']);
      },
      error: () => {
        this.vendaErrorMessage = 'Erro ao registrar venda';
        this.vendaLoading = false;
      }
    });
  }
}
