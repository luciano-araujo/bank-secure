import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApoliceService } from '../../../services/apolice.service';
import { Apolice } from '../../../models/apolice.model';

@Component({
  selector: 'app-lista',
  imports: [CommonModule],
  templateUrl: './lista.component.html',
  styleUrl: './lista.component.scss'
})
export class ListaComponent implements OnInit {
  apolices: Apolice[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private apoliceService: ApoliceService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.carregarApolices();
  }

  carregarApolices(): void {
    this.loading = true;
    this.apoliceService.listar().subscribe({
      next: (apolices) => {
        this.apolices = apolices;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar apólices';
        this.loading = false;
      }
    });
  }

  voltar(): void {
    this.router.navigate(['/home']);
  }

  novaApolice(): void {
    this.router.navigate(['/apolices/novo']);
  }
}
