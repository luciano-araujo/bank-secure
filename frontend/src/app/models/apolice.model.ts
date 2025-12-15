export interface Apolice {
    id?: number;
    clienteId: number;
    seguroId: number;
    totalCobertura: number;
    dataInicial: string;
    dataVencimento: string;
}

export interface ApoliceDashboard {
    totalApolices: number;
    valorTotalCobertura: number;
    apolicesAtivas: number;
    apolicesVencidas: number;
}
