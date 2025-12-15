import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SeguroService } from '../../../services/seguro.service';
import { Seguro } from '../../../models/seguro.model';

@Component({
  selector: 'app-form',
  imports: [CommonModule, FormsModule],
  templateUrl: './form.component.html',
  styleUrl: './form.component.scss'
})
export class FormComponent implements OnInit {
  seguro: Seguro = {
    titulo: '',
    coberturaMinima: '',
    valorPremioBase: 0
  };
  isEditMode = false;
  loading = false;
  errorMessage = '';
  seguroId?: number;

  constructor(
    private seguroService: SeguroService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.seguroId = +id;
      this.carregarSeguro(this.seguroId);
    }
  }

  carregarSeguro(id: number): void {
    this.loading = true;
    this.seguroService.buscarPorId(id).subscribe({
      next: (seguro) => {
        this.seguro = seguro;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar seguro';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';

    if (this.isEditMode && this.seguroId) {
      this.seguroService.atualizar(this.seguroId, this.seguro).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/seguros']);
        },
        error: () => {
          this.errorMessage = 'Erro ao atualizar seguro';
          this.loading = false;
        }
      });
    } else {
      this.seguroService.criar(this.seguro).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/seguros']);
        },
        error: () => {
          this.errorMessage = 'Erro ao criar seguro';
          this.loading = false;
        }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/seguros']);
  }
}
