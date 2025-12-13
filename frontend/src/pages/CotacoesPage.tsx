import { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import CalculateRoundedIcon from '@mui/icons-material/CalculateRounded';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import SectionHeader from '../components/SectionHeader';
import LoadingState from '../components/LoadingState';
import api from '../services/api';
import { fetchClientes, fetchCotacoes, fetchSeguros } from '../services/endpoints';
import type { Cliente, Cotacao, Seguro } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';
import { getErrorMessage } from '../utils/errorMessage';

type CotacaoFormState = {
  clienteId: string;
  seguroId: string;
  coberturaTotal: string;
};

const CotacoesPage = () => {
  const queryClient = useQueryClient();
  const { data: clientes } = useQuery<Cliente[]>({ queryKey: ['clientes'], queryFn: fetchClientes });
  const { data: seguros } = useQuery<Seguro[]>({ queryKey: ['seguros'], queryFn: fetchSeguros });
  const { data: cotacoes, isLoading } = useQuery<Cotacao[]>({
    queryKey: ['cotacoes'],
    queryFn: fetchCotacoes,
  });

  const [formValues, setFormValues] = useState<CotacaoFormState>({ clienteId: '', seguroId: '', coberturaTotal: '' });
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const generateMutation = useMutation({
    mutationFn: async (payload: CotacaoFormState) =>
      (
        await api.post<Cotacao>('/cotacao', {
          clienteId: payload.clienteId,
          seguroId: payload.seguroId,
          coberturaTotal: Number(payload.coberturaTotal),
        })
      ).data,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cotacoes'] });
      setErrorMessage(null);
    },
    onError: (error) => setErrorMessage(getErrorMessage(error)),
  });

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    generateMutation.mutate(formValues);
  };

  return (
    <Stack spacing={3}>
      <SectionHeader
        title="Cotações inteligentes"
        subtitle="Selecione um cliente, um seguro e informe o valor total dos bens para estimar o prêmio final."
      />

      {errorMessage && (
        <Alert severity="error" onClose={() => setErrorMessage(null)}>
          {errorMessage}
        </Alert>
      )}

      <Paper sx={{ p: 3, borderRadius: 4 }}>
        <Box component="form" onSubmit={handleSubmit}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
            <FormControl fullWidth>
              <InputLabel id="cotacao-cliente-label">Cliente</InputLabel>
              <Select
                labelId="cotacao-cliente-label"
                label="Cliente"
                value={formValues.clienteId}
                onChange={(event) => setFormValues((prev) => ({ ...prev, clienteId: event.target.value }))}
                required
              >
                {clientes?.map((cliente) => (
                  <MenuItem key={cliente.id} value={cliente.id}>
                    {cliente.nome}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <FormControl fullWidth>
              <InputLabel id="cotacao-seguro-label">Seguro</InputLabel>
              <Select
                labelId="cotacao-seguro-label"
                label="Seguro"
                value={formValues.seguroId}
                onChange={(event) => setFormValues((prev) => ({ ...prev, seguroId: event.target.value }))}
                required
              >
                {seguros?.map((seguro) => (
                  <MenuItem key={seguro.id} value={seguro.id}>
                    {seguro.titulo}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <TextField
              label="Valor total dos bens"
              type="number"
              value={formValues.coberturaTotal}
              onChange={(event) => setFormValues((prev) => ({ ...prev, coberturaTotal: event.target.value }))}
              required
              fullWidth
            />
            <Button
              type="submit"
              variant="contained"
              size="large"
              startIcon={<CalculateRoundedIcon />}
              disabled={generateMutation.isPending}
              sx={{ minWidth: 220 }}
            >
              {generateMutation.isPending ? 'Calculando...' : 'Gerar cotação'}
            </Button>
          </Stack>
        </Box>
      </Paper>

      <Paper sx={{ borderRadius: 4 }}>
        {isLoading ? (
          <LoadingState />
        ) : (
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Cliente</TableCell>
                  <TableCell>Seguro</TableCell>
                  <TableCell>Prêmio base</TableCell>
                  <TableCell>Prêmio final</TableCell>
                  <TableCell>Calculada em</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {cotacoes?.map((cotacao) => (
                  <TableRow key={cotacao.id}>
                    <TableCell>{cotacao.id.slice(0, 8)}</TableCell>
                    <TableCell>
                      {clientes?.find((cliente) => cliente.id === cotacao.clienteId)?.nome ?? cotacao.clienteId.slice(0, 8)}
                    </TableCell>
                    <TableCell>
                      {seguros?.find((seguro) => seguro.id === cotacao.seguroId)?.titulo ?? cotacao.seguroId.slice(0, 8)}
                    </TableCell>
                    <TableCell>{formatCurrency(cotacao.premioBase)}</TableCell>
                    <TableCell>{formatCurrency(cotacao.premioFinal)}</TableCell>
                    <TableCell>{formatDate(cotacao.dataCalculo)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Paper>

      {cotacoes && cotacoes.length === 0 && (
        <Paper sx={{ p: 3, borderRadius: 4 }}>
          <Typography variant="body2" color="text.secondary">
            Assim que uma cotação for gerada, ela aparecerá aqui com todos os detalhes financeiros.
          </Typography>
        </Paper>
      )}
    </Stack>
  );
};

export default CotacoesPage;
