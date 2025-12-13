import { useMemo } from 'react';
import { Box, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import SavingsRoundedIcon from '@mui/icons-material/SavingsRounded';
import ShieldRoundedIcon from '@mui/icons-material/ShieldRounded';
import TrendingUpRoundedIcon from '@mui/icons-material/TrendingUpRounded';
import AccessTimeRoundedIcon from '@mui/icons-material/AccessTimeRounded';
import { useQuery } from '@tanstack/react-query';
import MetricCard from '../components/MetricCard';
import SectionHeader from '../components/SectionHeader';
import LoadingState from '../components/LoadingState';
import EmptyState from '../components/EmptyState';
import { formatCurrency, formatDate } from '../utils/formatters';
import { fetchApolicesAVencer, fetchClientes, fetchCotacoes, fetchDashboard, fetchSeguros } from '../services/endpoints';
import type { Apolice, Cliente, Cotacao, DashboardItem, Seguro } from '../types';

const DashboardPage = () => {
  const { data: dashboardData, isLoading: isLoadingDashboard } = useQuery<DashboardItem[]>({
    queryKey: ['dashboard'],
    queryFn: fetchDashboard,
  });

  const { data: expiringPolicies, isLoading: isLoadingApolices } = useQuery<Apolice[]>({
    queryKey: ['apolices-vencer'],
    queryFn: fetchApolicesAVencer,
  });

  const { data: cotacoes, isLoading: isLoadingCotacoes } = useQuery<Cotacao[]>({
    queryKey: ['cotacoes'],
    queryFn: fetchCotacoes,
  });

  const { data: clientes } = useQuery<Cliente[]>({
    queryKey: ['clientes'],
    queryFn: fetchClientes,
  });

  const { data: seguros } = useQuery<Seguro[]>({
    queryKey: ['seguros'],
    queryFn: fetchSeguros,
  });

  const stats = useMemo(() => {
    const totalPremios =
      dashboardData?.reduce((total, card) => total + Number(card.valorTotalArrecadado ?? 0), 0) ?? 0;
    const quantidadeTipos = dashboardData?.length ?? 0;
    const totalApolices =
      dashboardData?.reduce((total, card) => total + Number(card.quantidadeApolices ?? 0), 0) ?? 0;
    const proximasRenovacoes = expiringPolicies?.length ?? 0;

    return { totalPremios, quantidadeTipos, totalApolices, proximasRenovacoes };
  }, [dashboardData, expiringPolicies]);

  const clienteMap = useMemo(
    () => new Map(clientes?.map((cliente) => [cliente.id, cliente.nome])),
    [clientes],
  );

  const seguroMap = useMemo(
    () => new Map(seguros?.map((seguro) => [seguro.id, seguro.titulo])),
    [seguros],
  );

  return (
    <Stack spacing={4}>
      <SectionHeader
        title="Painel executivo"
        subtitle="Acompanhe os indicadores de apólices, cotações e portfólio de seguros em tempo real."
      />

      <Box
        sx={{
          display: 'grid',
          gap: 3,
          gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, 1fr)', lg: 'repeat(4, 1fr)' },
        }}
      >
        <MetricCard
          label="Prêmios arrecadados"
          value={formatCurrency(stats.totalPremios)}
          helperText="Somatório por tipo de seguro"
          icon={<SavingsRoundedIcon />}
          loading={isLoadingDashboard}
        />
        <MetricCard
          label="Apólices ativas"
          value={`${stats.totalApolices}`}
          helperText="Distribuídas por produto"
          icon={<ShieldRoundedIcon />}
          loading={isLoadingDashboard}
        />
        <MetricCard
          label="Linhas de seguro"
          value={`${stats.quantidadeTipos}`}
          helperText="Produtos cadastrados"
          icon={<TrendingUpRoundedIcon />}
          loading={isLoadingDashboard}
        />
        <MetricCard
          label="Renovações próximas"
          value={`${stats.proximasRenovacoes}`}
          helperText="Até 30 dias"
          icon={<AccessTimeRoundedIcon />}
          loading={isLoadingApolices}
        />
      </Box>

      <Box
        sx={{
          display: 'grid',
          gap: 3,
          gridTemplateColumns: { xs: '1fr', md: 'repeat(2, 1fr)' },
        }}
      >
        <Paper sx={{ p: 3, borderRadius: 4, minHeight: 320 }}>
          <Typography variant="h6" gutterBottom>
            Performance por produto
          </Typography>
          {isLoadingDashboard && <LoadingState />}
          {!isLoadingDashboard && dashboardData && dashboardData.length > 0 ? (
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Seguro</TableCell>
                  <TableCell align="right">Apólices</TableCell>
                  <TableCell align="right">Prêmio total</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {dashboardData.map((item) => (
                  <TableRow key={item.tipoSeguro}>
                    <TableCell>{item.tipoSeguro}</TableCell>
                    <TableCell align="right">{item.quantidadeApolices}</TableCell>
                    <TableCell align="right">{formatCurrency(item.valorTotalArrecadado)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          ) : (
            !isLoadingDashboard && (
              <EmptyState
                title="Nenhum dado disponível"
                description="Cadastre apólices para começar a acompanhar a performance dos produtos."
              />
            )
          )}
        </Paper>

        <Paper sx={{ p: 3, borderRadius: 4, minHeight: 320 }}>
          <Typography variant="h6" gutterBottom>
            Apólices a vencer
          </Typography>
          {isLoadingApolices && <LoadingState />}
          {!isLoadingApolices && expiringPolicies && expiringPolicies.length > 0 ? (
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>ID da Apólice</TableCell>
                  <TableCell>Cliente</TableCell>
                  <TableCell>Vencimento</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {expiringPolicies.map((apolice) => (
                  <TableRow key={apolice.id}>
                    <TableCell>{apolice.id.slice(0, 8)}</TableCell>
                    <TableCell>{clienteMap.get(apolice.clienteId) ?? apolice.clienteId.slice(0, 8)}</TableCell>
                    <TableCell>{formatDate(apolice.dataVencimento)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          ) : (
            !isLoadingApolices && (
              <EmptyState title="Tudo em dia!" description="Nenhuma apólice vence nos próximos 30 dias." />
            )
          )}
        </Paper>
      </Box>

      <Paper sx={{ p: 3, borderRadius: 4 }}>
        <Typography variant="h6" gutterBottom>
          Últimas cotações geradas
        </Typography>
        {isLoadingCotacoes && <LoadingState />}
        {!isLoadingCotacoes && cotacoes && cotacoes.length > 0 ? (
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Cliente</TableCell>
                <TableCell>Seguro</TableCell>
                <TableCell>Prêmio base</TableCell>
                <TableCell>Prêmio final</TableCell>
                <TableCell>Data</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {cotacoes.slice(0, 6).map((cotacao) => (
                <TableRow key={cotacao.id}>
                  <TableCell>{cotacao.id.slice(0, 8)}</TableCell>
                  <TableCell>{clienteMap.get(cotacao.clienteId) ?? cotacao.clienteId.slice(0, 8)}</TableCell>
                  <TableCell>{seguroMap.get(cotacao.seguroId) ?? cotacao.seguroId.slice(0, 8)}</TableCell>
                  <TableCell>{formatCurrency(cotacao.premioBase)}</TableCell>
                  <TableCell>{formatCurrency(cotacao.premioFinal)}</TableCell>
                  <TableCell>{formatDate(cotacao.dataCalculo)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : (
          !isLoadingCotacoes && (
            <EmptyState
              title="Sem cotações"
              description="Realize uma nova cotação para preenchermos este painel."
            />
          )
        )}
      </Paper>
    </Stack>
  );
};

export default DashboardPage;

