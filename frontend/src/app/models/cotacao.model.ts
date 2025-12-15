export interface Cotacao {
    id?: number;
    clienteId: number;
    seguroId: number;
    premioBase: number;
    premioFinal: number;
    dataCalculo: string;
}

export interface CotacaoRequest {
    clienteId: number;
    seguroId: number;
}
