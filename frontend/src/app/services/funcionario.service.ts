import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Funcionario } from '../models/funcionario.model';

@Injectable({
    providedIn: 'root'
})
export class FuncionarioService {
    private apiUrl = 'http://localhost:8080/funcionario';

    constructor(private http: HttpClient) { }

    listar(): Observable<Funcionario[]> {
        return this.http.get<Funcionario[]>(this.apiUrl);
    }

    buscarPorId(id: string): Observable<Funcionario> {
        return this.http.get<Funcionario>(`${this.apiUrl}/${id}`);
    }

    criar(funcionario: Funcionario): Observable<Funcionario> {
        return this.http.post<Funcionario>(this.apiUrl, funcionario);
    }

    atualizar(id: string, funcionario: Funcionario): Observable<Funcionario> {
        return this.http.put<Funcionario>(`${this.apiUrl}/${id}`, funcionario);
    }

    deletar(id: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
