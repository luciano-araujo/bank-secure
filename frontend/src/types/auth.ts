export type TipoUsuario = 'CLIENTE' | 'FUNCIONARIO';

export interface AuthRequest {
  email: string;
  senha: string;
}

export interface AuthResponse {
  authenticated: boolean;
  usuarioId: string;
  nome: string;
  tipoUsuario: TipoUsuario;
}

