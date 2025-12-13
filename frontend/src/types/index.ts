export type UUID = string;

export interface Cliente {
  id: UUID;
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
  dataNascimento: string;
  senha?: string;
}

export interface Funcionario {
  id: UUID;
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
  dataNascimento: string;
  senha?: string;
}

export type TipoSeguro = 'RESIDENCIAL' | 'AUTOMOTIVO' | 'VIDA';

export interface Seguro {
  id: UUID;
  titulo: string;
  tipo: TipoSeguro;
  coberturaMinima: number;
  valorPremioBase: number;
}

export interface Bem {
  id: UUID;
  titulo: string;
  valor: number;
  apoliceId: UUID;
}

export interface NewBem {
  titulo: string;
  valor: number;
}

export interface Apolice {
  id: UUID;
  clienteId: UUID;
  seguroId: UUID;
  tipoSeguro: TipoSeguro;
  totalCobertura: number;
  premioFinal: number;
  dataInicial: string;
  dataVencimento: string;
  bens: Bem[];
}

export interface NovaApolicePayload {
  clienteId: UUID;
  seguroId: UUID;
  dataInicial: string;
  dataVencimento: string;
  bens: Array<{ titulo: string; valor: number }>;
}

export interface Cotacao {
  id: UUID;
  clienteId: UUID;
  seguroId: UUID;
  premioBase: number;
  premioFinal: number;
  dataCalculo: string;
}

export interface CotacaoRequest {
  clienteId: UUID;
  seguroId: UUID;
  coberturaTotal: number;
}

export interface DashboardItem {
  tipoSeguro: string;
  quantidadeApolices: number;
  valorTotalArrecadado: number;
}
