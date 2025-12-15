import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cotacao, CotacaoRequest } from '../models/cotacao.model';

@Injectable({
    providedIn: 'root'
})
export class CotacaoService {
    private apiUrl = 'http://localhost:8080/cotacao';

    constructor(private http: HttpClient) { }

    listar(): Observable<Cotacao[]> {
        return this.http.get<Cotacao[]>(this.apiUrl);
    }

    realizar(request: CotacaoRequest): Observable<Cotacao> {
        return this.http.post<Cotacao>(this.apiUrl, request);
    }
}
