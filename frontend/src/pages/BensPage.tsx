import { useState } from 'react';
import {
  Alert,
  Button,
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
} from '@mui/material';
import AddRoundedIcon from '@mui/icons-material/AddRounded';
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import SectionHeader from '../components/SectionHeader';
import EntityDialog from '../components/EntityDialog';
import LoadingState from '../components/LoadingState';
import api from '../services/api';
import { fetchApolices, fetchBens } from '../services/endpoints';
import type { Apolice, Bem } from '../types';
import { formatCurrency } from '../utils/formatters';
import { getErrorMessage } from '../utils/errorMessage';

type BemFormState = {
  titulo: string;
  valor: string;
  apoliceId: string;
};

const emptyForm: BemFormState = {
  titulo: '',
  valor: '',
  apoliceId: '',
};

const BensPage = () => {
  const queryClient = useQueryClient();
  const { data: bens, isLoading } = useQuery<Bem[]>({
    queryKey: ['bens'],
    queryFn: fetchBens,
  });

  const { data: apolices } = useQuery<Apolice[]>({
    queryKey: ['apolices'],
    queryFn: fetchApolices,
  });

  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingBem, setEditingBem] = useState<Bem | null>(null);
  const [formValues, setFormValues] = useState<BemFormState>(emptyForm);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const saveMutation = useMutation({
    mutationFn: async (payload: BemFormState) => {
      if (editingBem) {
        return (
          await api.put<Bem>(`/bem/${editingBem.id}`, {
            titulo: payload.titulo,
            valor: Number(payload.valor),
            apoliceId: editingBem.apoliceId,
          })
        ).data;
      }

      return (
        await api.post<Bem>('/bem', {
          titulo: payload.titulo,
          valor: Number(payload.valor),
          apoliceId: payload.apoliceId,
        })
      ).data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['bens'] });
      setIsDialogOpen(false);
      setEditingBem(null);
      setFormValues(emptyForm);
      setErrorMessage(null);
    },
    onError: (error) => setErrorMessage(getErrorMessage(error)),
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: string) => api.delete(`/bem/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['bens'] }),
    onError: (error) => setErrorMessage(getErrorMessage(error)),
  });

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    saveMutation.mutate(formValues);
  };

  return (
    <Stack spacing={3}>
      <SectionHeader
        title="Bens segurados"
        subtitle="Registre bens vinculados às apólices e acompanhe seus valores segurados."
        action={
          <Button
            variant="contained"
            startIcon={<AddRoundedIcon />}
            onClick={() => {
              setEditingBem(null);
              setFormValues(emptyForm);
              setIsDialogOpen(true);
            }}
          >
            Novo bem
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
                  <TableCell>Título</TableCell>
                  <TableCell>Valor segurado</TableCell>
                  <TableCell>Apólice</TableCell>
                  <TableCell width={120} align="center">
                    Ações
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {bens?.map((bem) => (
                  <TableRow key={bem.id}>
                    <TableCell>{bem.titulo}</TableCell>
                    <TableCell>{formatCurrency(bem.valor)}</TableCell>
                    <TableCell>{bem.apoliceId.slice(0, 8)}</TableCell>
                    <TableCell align="center">
                      <Tooltip title="Editar">
                        <IconButton
                          size="small"
                          color="primary"
                          onClick={() => {
                            setEditingBem(bem);
                            setFormValues({
                              titulo: bem.titulo,
                              valor: String(bem.valor ?? ''),
                              apoliceId: bem.apoliceId,
                            });
                            setIsDialogOpen(true);
                          }}
                        >
                          <EditRoundedIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Excluir">
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => deleteMutation.mutate(bem.id)}
                          disabled={deleteMutation.isPending}
                        >
                          <DeleteRoundedIcon fontSize="small" />
                        </IconButton>
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
        title={editingBem ? 'Editar bem' : 'Novo bem'}
        onClose={() => {
          setIsDialogOpen(false);
          setEditingBem(null);
        }}
        onSubmit={handleSubmit}
        isSubmitting={saveMutation.isPending}
      >
        <TextField
          label="Título do bem"
          value={formValues.titulo}
          required
          onChange={(event) => setFormValues((prev) => ({ ...prev, titulo: event.target.value }))}
        />
        <TextField
          label="Valor segurado"
          type="number"
          required
          value={formValues.valor}
          onChange={(event) => setFormValues((prev) => ({ ...prev, valor: event.target.value }))}
        />
        <FormControl fullWidth disabled={Boolean(editingBem)}>
          <InputLabel id="apolice-select-label">Apólice</InputLabel>
          <Select
            labelId="apolice-select-label"
            label="Apólice"
            value={formValues.apoliceId}
            onChange={(event) => setFormValues((prev) => ({ ...prev, apoliceId: event.target.value }))}
            required
          >
            {apolices?.map((apolice) => (
              <MenuItem key={apolice.id} value={apolice.id}>
                {apolice.id.slice(0, 8)} — {formatCurrency(apolice.totalCobertura)}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </EntityDialog>
    </Stack>
  );
};

export default BensPage;
