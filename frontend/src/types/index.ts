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

export interface Seguro {
  id: UUID;
  titulo: string;
  coberturaMinima: number;
  valorPremioBase: number;
}

export interface Apolice {
  id: UUID;
  clienteId: UUID;
  seguroId: UUID;
  totalCobertura: number;
  dataInicial: string;
  dataVencimento: string;
}

export interface Bem {
  id: UUID;
  titulo: string;
  valor: number;
  apoliceId: UUID;
}

export interface Cotacao {
  id: UUID;
  clienteId: UUID;
  seguroId: UUID;
  premioBase: number;
  premioFinal: number;
  dataCalculo: string;
}

export interface DashboardItem {
  tipoSeguro: string;
  quantidadeApolices: number;
  valorTotalArrecadado: number;
}

