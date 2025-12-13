import { useMemo, useState } from 'react';
import {
  Alert,
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
} from '@mui/material';
import AddRoundedIcon from '@mui/icons-material/AddRounded';
import ReplayRoundedIcon from '@mui/icons-material/ReplayRounded';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import SectionHeader from '../components/SectionHeader';
import EntityDialog from '../components/EntityDialog';
import LoadingState from '../components/LoadingState';
import api from '../services/api';
import { fetchApolices, fetchClientes, fetchSeguros } from '../services/endpoints';
import type { Apolice, Cliente, Seguro } from '../types';
import { formatCurrency, formatDate, normalizeDateInput } from '../utils/formatters';
import { getErrorMessage } from '../utils/errorMessage';

type ApoliceFormState = {
  clienteId: string;
  seguroId: string;
  totalCobertura: string;
  dataInicial: string;
  dataVencimento: string;
};

const emptyForm: ApoliceFormState = {
  clienteId: '',
  seguroId: '',
  totalCobertura: '',
  dataInicial: '',
  dataVencimento: '',
};

const ApolicesPage = () => {
  const queryClient = useQueryClient();
  const { data: apolices, isLoading } = useQuery<Apolice[]>({
    queryKey: ['apolices'],
    queryFn: fetchApolices,
  });

  const { data: clientes } = useQuery<Cliente[]>({
    queryKey: ['clientes'],
    queryFn: fetchClientes,
  });

  const { data: seguros } = useQuery<Seguro[]>({
    queryKey: ['seguros'],
    queryFn: fetchSeguros,
  });

  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [formValues, setFormValues] = useState<ApoliceFormState>(emptyForm);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const createMutation = useMutation({
    mutationFn: async (payload: ApoliceFormState) =>
      (
        await api.post<Apolice>('/apolice', {
          ...payload,
          totalCobertura: Number(payload.totalCobertura),
        })
      ).data,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['apolices'] });
      setIsDialogOpen(false);
      setFormValues(emptyForm);
      setErrorMessage(null);
    },
    onError: (error) => setErrorMessage(getErrorMessage(error)),
  });

  const renewMutation = useMutation({
    mutationFn: async (id: string) => (await api.post<Apolice>(`/apolice/renovacao/${id}`)).data,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['apolices'] });
    },
    onError: (error) => setErrorMessage(getErrorMessage(error)),
  });

  const customerLookup = useMemo(() => new Map(clientes?.map((cliente) => [cliente.id, cliente.nome])), [clientes]);
  const productLookup = useMemo(() => new Map(seguros?.map((seguro) => [seguro.id, seguro.titulo])), [seguros]);

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    createMutation.mutate(formValues);
  };

  return (
    <Stack spacing={3}>
      <SectionHeader
        title="Apólices"
        subtitle="Controle as apólices vigentes, total de cobertura e datas de vigência."
        action={
          <Button
            variant="contained"
            startIcon={<AddRoundedIcon />}
            onClick={() => {
              setFormValues({
                ...emptyForm,
                dataInicial: normalizeDateInput(new Date().toISOString()),
                dataVencimento: '',
              });
              setIsDialogOpen(true);
            }}
          >
            Nova apólice
          </Button>
        }
      />

      {errorMessage && (
        <Alert severity="error" onClose={() => setErrorMessage(null)}>
          {errorMessage}
        </Alert>
      )}

      <Paper sx={{ borderRadius: 4 }}>
        {isLoading ? (
          <LoadingState />
        ) : (
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Cliente</TableCell>
                  <TableCell>Produto</TableCell>
                  <TableCell>Cobertura</TableCell>
                  <TableCell>Início</TableCell>
                  <TableCell>Vencimento</TableCell>
                  <TableCell width={120} align="center">
                    Renovar
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {apolices?.map((apolice) => (
                  <TableRow key={apolice.id}>
                    <TableCell>{customerLookup.get(apolice.clienteId) ?? apolice.clienteId.slice(0, 8)}</TableCell>
                    <TableCell>{productLookup.get(apolice.seguroId) ?? apolice.seguroId.slice(0, 8)}</TableCell>
                    <TableCell>{formatCurrency(apolice.totalCobertura)}</TableCell>
                    <TableCell>{formatDate(apolice.dataInicial)}</TableCell>
                    <TableCell>{formatDate(apolice.dataVencimento)}</TableCell>
                    <TableCell align="center">
                      <Button
                        size="small"
                        variant="outlined"
                        startIcon={<ReplayRoundedIcon fontSize="small" />}
                        onClick={() => renewMutation.mutate(apolice.id)}
                        disabled={renewMutation.isPending}
                      >
                        Renovar
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Paper>

      <EntityDialog
        open={isDialogOpen}
        title="Cadastro de apólice"
        onClose={() => setIsDialogOpen(false)}
        onSubmit={handleSubmit}
        isSubmitting={createMutation.isPending}
      >
        <FormControl fullWidth>
          <InputLabel id="cliente-label">Cliente</InputLabel>
          <Select
            labelId="cliente-label"
            value={formValues.clienteId}
            label="Cliente"
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
          <InputLabel id="seguro-label">Seguro</InputLabel>
          <Select
            labelId="seguro-label"
            value={formValues.seguroId}
            label="Seguro"
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
          label="Total de cobertura"
          type="number"
          required
          value={formValues.totalCobertura}
          onChange={(event) => setFormValues((prev) => ({ ...prev, totalCobertura: event.target.value }))}
        />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Data inicial"
            type="date"
            InputLabelProps={{ shrink: true }}
            required
            value={formValues.dataInicial}
            onChange={(event) => setFormValues((prev) => ({ ...prev, dataInicial: event.target.value }))}
          />
          <TextField
            label="Data de vencimento"
            type="date"
            InputLabelProps={{ shrink: true }}
            required
            value={formValues.dataVencimento}
            onChange={(event) => setFormValues((prev) => ({ ...prev, dataVencimento: event.target.value }))}
          />
        </Stack>
      </EntityDialog>
    </Stack>
  );
};

export default ApolicesPage;

