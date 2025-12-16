import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Apolice, ApoliceDashboard, DashboardPorTipo } from '../models/apolice.model';

@Injectable({
    providedIn: 'root'
})
export class ApoliceService {
    private apiUrl = 'http://localhost:8080/apolice';

    constructor(private http: HttpClient) { }

    listar(): Observable<Apolice[]> {
        return this.http.get<Apolice[]>(this.apiUrl);
    }

    criar(apolice: Apolice): Observable<Apolice> {
        return this.http.post<Apolice>(this.apiUrl, apolice);
    }

    getDashboard(): Observable<ApoliceDashboard> {
        return this.http.get<ApoliceDashboard>(`${this.apiUrl}/dashboard`);
    }

    getDashboardPorTipo(): Observable<DashboardPorTipo[]> {
        return this.http.get<DashboardPorTipo[]>(`${this.apiUrl}/dashboard`);
    }

    renovar(id: number): Observable<Apolice> {
        return this.http.post<Apolice>(`${this.apiUrl}/renovacao/${id}`, {});
    }

    listarApolicesVencidas(): Observable<Apolice[]> {
      return this.http.get<Apolice[]>(`${this.apiUrl}/vencidas`);
    }

    listarApolicesAVencer(): Observable<Apolice[]> {
      return this.http.get<Apolice[]>(`${this.apiUrl}/vencer`);
    }
}
