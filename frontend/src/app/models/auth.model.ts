export interface AuthRequest {
    email: string;
    senha: string;
}

export interface AuthResponse {
    authenticated: boolean;
    usuarioId: number;
    nome: string;
    tipoUsuario: 'FUNCIONARIO' | 'CLIENTE';
}

export interface User {
    id: number;
    nome: string;
    tipo: 'FUNCIONARIO' | 'CLIENTE';
}
