import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Seguro } from '../models/seguro.model';

@Injectable({
    providedIn: 'root'
})
export class SeguroService {
    private apiUrl = 'http://localhost:8080/seguro';

    constructor(private http: HttpClient) { }

    listar(): Observable<Seguro[]> {
        return this.http.get<Seguro[]>(this.apiUrl);
    }

    buscarPorId(id: number): Observable<Seguro> {
        return this.http.get<Seguro>(`${this.apiUrl}/${id}`);
    }

    criar(seguro: Seguro): Observable<Seguro> {
        return this.http.post<Seguro>(this.apiUrl, seguro);
    }

    atualizar(id: number, seguro: Seguro): Observable<Seguro> {
        return this.http.put<Seguro>(`${this.apiUrl}/${id}`, seguro);
    }

    deletar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
