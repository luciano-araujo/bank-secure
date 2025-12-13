import { useMemo, useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  FormControl,
  IconButton,
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
  Tooltip,
  Typography,
} from '@mui/material';
import AddRoundedIcon from '@mui/icons-material/AddRounded';
import ReplayRoundedIcon from '@mui/icons-material/ReplayRounded';
import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import SectionHeader from '../components/SectionHeader';
import EntityDialog from '../components/EntityDialog';
import LoadingState from '../components/LoadingState';
import api from '../services/api';
import { fetchApolices, fetchClientes, fetchSeguros } from '../services/endpoints';
import type { Apolice, Cliente, NovaApolicePayload, Seguro } from '../types';
import { formatCurrency, formatDate, normalizeDateInput } from '../utils/formatters';
import { getErrorMessage } from '../utils/errorMessage';

type BemFormState = { titulo: string; valor: string };

type ApoliceFormState = {
  clienteId: string;
  seguroId: string;
  dataInicial: string;
  dataVencimento: string;
  bens: BemFormState[];
};

const emptyForm: ApoliceFormState = {
  clienteId: '',
  seguroId: '',
  dataInicial: '',
  dataVencimento: '',
  bens: [{ titulo: '', valor: '' }],
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
    mutationFn: async (payload: NovaApolicePayload) => (await api.post<Apolice>('/apolice', payload)).data,
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
  const productLookup = useMemo(() => new Map(seguros?.map((seguro) => [seguro.id, seguro])), [seguros]);

  const totalCobertura = formValues.bens.reduce(
    (sum, bem) => (sum += Number(bem.valor || 0)),
    0,
  );

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const bensPayload = formValues.bens
      .filter((bem) => bem.titulo.trim() && Number(bem.valor) > 0)
      .map((bem) => ({
        titulo: bem.titulo.trim(),
        valor: Number(bem.valor),
      }));

    if (!bensPayload.length) {
      setErrorMessage('Inclua ao menos um bem com valor positivo.');
      return;
    }

    const payload: NovaApolicePayload = {
      clienteId: formValues.clienteId,
      seguroId: formValues.seguroId,
      dataInicial: formValues.dataInicial,
      dataVencimento: formValues.dataVencimento,
      bens: bensPayload,
    };

    createMutation.mutate(payload);
  };

  const updateBem = (index: number, field: keyof BemFormState, value: string) => {
    setFormValues((prev) => ({
      ...prev,
      bens: prev.bens.map((bem, idx) => (idx === index ? { ...bem, [field]: value } : bem)),
    }));
  };

  const addBem = () => {
    setFormValues((prev) => ({ ...prev, bens: [...prev.bens, { titulo: '', valor: '' }] }));
  };

  const removeBem = (index: number) => {
    setFormValues((prev) => ({
      ...prev,
      bens: prev.bens.filter((_, idx) => idx !== index),
    }));
  };

  return (
    <Stack spacing={3}>
      <SectionHeader
        title="Apólices"
        subtitle="Controle as apólices vigentes, os bens segurados e acompanhe a vigência."
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
                  <TableCell>Seguro</TableCell>
                  <TableCell>Tipo</TableCell>
                  <TableCell>Cobertura</TableCell>
                  <TableCell>Prêmio</TableCell>
                  <TableCell>Período</TableCell>
                  <TableCell>Bens</TableCell>
                  <TableCell width={140} align="center">
                    Ações
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {apolices?.map((apolice) => (
                  <TableRow key={apolice.id}>
                    <TableCell>{customerLookup.get(apolice.clienteId) ?? apolice.clienteId.slice(0, 8)}</TableCell>
                    <TableCell>{productLookup.get(apolice.seguroId)?.titulo ?? apolice.seguroId.slice(0, 8)}</TableCell>
                    <TableCell>
                      <Chip label={apolice.tipoSeguro} size="small" color="secondary" />
                    </TableCell>
                    <TableCell>{formatCurrency(apolice.totalCobertura)}</TableCell>
                    <TableCell>{formatCurrency(apolice.premioFinal)}</TableCell>
                    <TableCell>
                      <Stack spacing={0.5}>
                        <span>{formatDate(apolice.dataInicial)}</span>
                        <span>{formatDate(apolice.dataVencimento)}</span>
                      </Stack>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">{apolice.bens.length} item(s)</Typography>
                      <Typography variant="caption" color="text.secondary">
                        {formatCurrency(apolice.bens.reduce((acc, bem) => acc + bem.valor, 0))}
                      </Typography>
                    </TableCell>
                    <TableCell align="center">
                      <Tooltip title="Renovar apólice">
                        <span>
                          <IconButton
                            color="primary"
                            size="small"
                            onClick={() => renewMutation.mutate(apolice.id)}
                            disabled={renewMutation.isPending}
                          >
                            <ReplayRoundedIcon fontSize="small" />
                          </IconButton>
                        </span>
                      </Tooltip>
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
        onClose={() => {
          setIsDialogOpen(false);
          setFormValues(emptyForm);
        }}
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

        <Box>
          <Typography variant="subtitle2" gutterBottom>
            Bens segurados
          </Typography>
          <Stack spacing={2}>
            {formValues.bens.map((bem, index) => (
              <Stack key={`bem-${index}`} direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="flex-start">
                <TextField
                  label="Título do bem"
                  value={bem.titulo}
                  onChange={(event) => updateBem(index, 'titulo', event.target.value)}
                  required
                  fullWidth
                />
                <TextField
                  label="Valor segurado"
                  type="number"
                  value={bem.valor}
                  onChange={(event) => updateBem(index, 'valor', event.target.value)}
                  required
                  fullWidth
                />
                {formValues.bens.length > 1 && (
                  <Tooltip title="Remover bem">
                    <IconButton color="error" onClick={() => removeBem(index)}>
                      <DeleteOutlineRoundedIcon />
                    </IconButton>
                  </Tooltip>
                )}
              </Stack>
            ))}
            <Button variant="outlined" size="small" onClick={addBem}>
              Adicionar bem
            </Button>
            <Typography variant="body2" color="text.secondary">
              Cobertura estimada: <strong>{formatCurrency(totalCobertura)}</strong>
            </Typography>
          </Stack>
        </Box>
      </EntityDialog>
    </Stack>
  );
};

export default ApolicesPage;
