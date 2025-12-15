export interface Seguro {
    id?: number;
    titulo: string;
    coberturaMinima: string;
    valorPremioBase: number;
}

export enum TipoSeguro {
    VIDA = 'VIDA',
    AUTOMOVEL = 'AUTOMOVEL',
    RESIDENCIAL = 'RESIDENCIAL',
    EMPRESARIAL = 'EMPRESARIAL'
}
