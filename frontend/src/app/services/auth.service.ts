import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthRequest, AuthResponse, User } from '../models/auth.model';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8080';
    private currentUserSubject = new BehaviorSubject<User | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    constructor(private http: HttpClient) {
        this.loadUserFromStorage();
    }

    login(credentials: AuthRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/auth`, credentials).pipe(
            tap(response => {
                if (response.authenticated) {
                    const user: User = {
                        id: response.usuarioId,
                        nome: response.nome,
                        tipo: response.tipoUsuario
                    };
                    this.setCurrentUser(user);
                }
            })
        );
    }

    logout(): void {
        localStorage.removeItem('currentUser');
        this.currentUserSubject.next(null);
    }

    private setCurrentUser(user: User): void {
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
    }

    private loadUserFromStorage(): void {
        const userStr = localStorage.getItem('currentUser');
        if (userStr) {
            try {
                const user = JSON.parse(userStr);
                this.currentUserSubject.next(user);
            } catch {
                localStorage.removeItem('currentUser');
            }
        }
    }

    getCurrentUser(): User | null {
        return this.currentUserSubject.value;
    }

    isAuthenticated(): boolean {
        return !!this.currentUserSubject.value;
    }

    isFuncionario(): boolean {
        const user = this.getCurrentUser();
        return user?.tipo === 'FUNCIONARIO';
    }
}
