import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApoliceService } from '../../../services/apolice.service';
import { Apolice } from '../../../models/apolice.model';
import { Cliente } from '../../../models/cliente.model';
import { Seguro } from '../../../models/seguro.model';
import { ClienteService } from '../../../services/cliente.service';
import { SeguroService } from '../../../services/seguro.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-lista',
  imports: [CommonModule],
  templateUrl: './lista.component.html',
  styleUrl: './lista.component.scss'
})
export class ListaComponent implements OnInit {
  apolices: Apolice[] = [];
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
    this.loading = true;
    forkJoin({
      clientes: this.clienteService.listar(),
      seguros: this.seguroService.listar(),
      apolices: this.apoliceService.listar()
    }).subscribe({
      next: ({ clientes, seguros, apolices }) => {
        this.clientes = clientes || [];
        this.seguros = seguros || [];
        this.apolices = apolices || [];
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar apólices, clientes ou seguros';
        this.loading = false;
      }
    });
  }

  imprimirApolice(apolice: Apolice): void {
    const cliente = this.clientes.find(c => String(c.id) === String(apolice.clienteId));
    const seguro = this.seguros.find(s => String(s.id) === String(apolice.seguroId));
    const win = window.open('', '_blank');
    if (win) {
      win.document.write(`
        <html>
        <head>
          <title>Apólice BankSecure - ${apolice.id}</title>
          <style>
            body { font-family: 'Arial', sans-serif; max-width: 800px; margin: 40px auto; padding: 20px; }
            .header { text-align: center; border-bottom: 3px solid #c41e3a; padding-bottom: 20px; margin-bottom: 30px; }
            .header h1 { color: #c41e3a; font-size: 2.5rem; margin: 0; }
            .header p { color: #666; margin: 5px 0; }
            .content { margin: 30px 0; }
            .info-row { display: flex; justify-content: space-between; margin: 15px 0; padding: 10px; background: #f8f9fa; border-radius: 5px; }
            .info-row label { font-weight: bold; color: #333; }
            .info-row span { color: #555; }
            .signatures { margin-top: 80px; display: flex; justify-content: space-around; }
            .signature-box { text-align: center; width: 250px; }
            .signature-line { border-top: 2px solid #333; margin-top: 60px; padding-top: 10px; }
            .footer { text-align: center; margin-top: 60px; color: #666; font-size: 0.9rem; border-top: 1px solid #ddd; padding-top: 20px; }
            .company-signature { font-weight: bold; color: #c41e3a; font-size: 1.1rem; }
          </style>
        </head>
        <body>
          <div class="header">
            <h1>BankSecure</h1>
            <p>Apólice de Seguro</p>
          </div>
          
          <div class="content">
            <div class="info-row">
              <label>Número da Apólice:</label>
              <span>${apolice.id}</span>
            </div>
            <div class="info-row">
              <label>Cliente:</label>
              <span>${cliente ? cliente.nome : apolice.clienteId}</span>
            </div>
            <div class="info-row">
              <label>Tipo de Seguro:</label>
              <span>${seguro ? seguro.titulo : apolice.seguroId}</span>
            </div>
            <div class="info-row">
              <label>Cobertura Total:</label>
              <span>R$ ${apolice.totalCobertura.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</span>
            </div>
            <div class="info-row">
              <label>Data de Início:</label>
              <span>${new Date(apolice.dataInicial).toLocaleDateString('pt-BR')}</span>
            </div>
            <div class="info-row">
              <label>Data de Vencimento:</label>
              <span>${new Date(apolice.dataVencimento).toLocaleDateString('pt-BR')}</span>
            </div>
          </div>

          <div class="signatures">
            <div class="signature-box">
              <div class="signature-line">Assinatura do Cliente</div>
            </div>
            <div class="signature-box">
              <div class="signature-line">Assinatura do Funcionário</div>
            </div>
          </div>

          <div class="footer">
            <p class="company-signature">BankSecure - Seguros e Proteção</p>
            <p>Este documento é válido mediante assinaturas de ambas as partes.</p>
          </div>
        </body>
        </html>
      `);
      win.print();
    }
  }

  apoliceProximaVencer(apolice: Apolice): boolean {
    const hoje = new Date();
    const venc = new Date(apolice.dataVencimento);
    const trintaDias = new Date();
    trintaDias.setDate(hoje.getDate() + 30);
    // Permite renovar se já venceu OU está dentro de 30 dias para vencer
    return venc <= trintaDias;
  }

  renovarApolice(apolice: Apolice): void {
    if (confirm(`Deseja renovar a apólice ${apolice.id}? Será criada uma nova apólice com vigência de 1 ano e o prêmio será recalculado.`)) {
      this.loading = true;
      this.apoliceService.renovar(apolice.id!).subscribe({
        next: (novaApolice) => {
          alert(`Apólice renovada com sucesso! Nova apólice ID: ${novaApolice.id}`);
          this.ngOnInit(); // Recarrega a lista
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Erro ao renovar apólice';
          alert(this.errorMessage);
          this.loading = false;
        }
      });
    }
  }

  getClienteNome(clienteId: string | number): string {
    const cliente = this.clientes.find(c => c.id == clienteId);
    return cliente ? cliente.nome : String(clienteId);
  }

  getSeguroTitulo(seguroId: string | number): string {
    const seguro = this.seguros.find(s => s.id == seguroId);
    return seguro ? seguro.titulo : String(seguroId);
  }

  voltar(): void {
    this.router.navigate(['/home']);
  }

  novaApolice(): void {
    this.router.navigate(['/apolices/novo']);
  }
}
