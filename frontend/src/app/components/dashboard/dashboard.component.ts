import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApoliceService } from '../../services/apolice.service';
import { ApoliceDashboard } from '../../models/apolice.model';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  dashboard?: ApoliceDashboard;
  loading = false;
  errorMessage = '';

  constructor(
    private apoliceService: ApoliceService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.carregarDashboard();
  }

  carregarDashboard(): void {
    this.loading = true;
    this.apoliceService.getDashboard().subscribe({
      next: (data) => {
        this.dashboard = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar dashboard';
        this.loading = false;
      }
    });
  }

  voltar(): void {
    this.router.navigate(['/home']);
  }
}
