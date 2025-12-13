import api from './api';
import type { Apolice, Bem, Cliente, Cotacao, DashboardItem, Funcionario, Seguro } from '../types';

export const fetchClientes = async () => (await api.get<Cliente[]>('/cliente')).data;
export const fetchFuncionarios = async () => (await api.get<Funcionario[]>('/funcionario')).data;
export const fetchSeguros = async () => (await api.get<Seguro[]>('/seguro')).data;
export const fetchApolices = async () => (await api.get<Apolice[]>('/apolice')).data;
export const fetchBens = async () => (await api.get<Bem[]>('/bem')).data;
export const fetchCotacoes = async () => (await api.get<Cotacao[]>('/cotacao')).data;
export const fetchDashboard = async () => (await api.get<DashboardItem[]>('/apolice/dashboard')).data;
export const fetchApolicesAVencer = async () => (await api.get<Apolice[]>('/apolice/vencer')).data;

