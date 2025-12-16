import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApoliceService } from '../../services/apolice.service';
import { DashboardPorTipo } from '../../models/apolice.model';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  dashboardPorTipo: DashboardPorTipo[] = [];
  loading = false;
  errorMessage = '';
  totalGeral = 0;
  valorTotalGeral = 0;

  constructor(
    private apoliceService: ApoliceService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.carregarDashboard();
  }

  carregarDashboard(): void {
    this.loading = true;
    this.apoliceService.getDashboardPorTipo().subscribe({
      next: (data) => {
        this.dashboardPorTipo = data;
        this.calcularTotais();
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar dashboard';
        this.loading = false;
      }
    });
  }

  calcularTotais(): void {
    this.totalGeral = this.dashboardPorTipo.reduce((sum, item) => sum + item.quantidadeApolices, 0);
    this.valorTotalGeral = this.dashboardPorTipo.reduce((sum, item) => sum + item.valorTotalArrecadado, 0);
  }

  voltar(): void {
    this.router.navigate(['/home']);
  }
}
