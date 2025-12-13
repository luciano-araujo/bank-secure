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
import { fetchSeguros } from '../services/endpoints';
import type { Seguro, TipoSeguro } from '../types';
import { formatCurrency } from '../utils/formatters';
import { getErrorMessage } from '../utils/errorMessage';

type SeguroFormState = {
  titulo: string;
  tipo: TipoSeguro | '';
  coberturaMinima: string;
  valorPremioBase: string;
};

const emptyForm: SeguroFormState = {
  titulo: '',
  tipo: '',
  coberturaMinima: '',
  valorPremioBase: '',
};

const tipoOptions: { label: string; value: TipoSeguro }[] = [
  { label: 'Residencial', value: 'RESIDENCIAL' },
  { label: 'Automotivo', value: 'AUTOMOTIVO' },
  { label: 'Vida', value: 'VIDA' },
];

const SegurosPage = () => {
  const queryClient = useQueryClient();

  const { data: seguros, isLoading } = useQuery<Seguro[]>({
    queryKey: ['seguros'],
    queryFn: fetchSeguros,
  });

  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingSeguro, setEditingSeguro] = useState<Seguro | null>(null);
  const [formValues, setFormValues] = useState<SeguroFormState>(emptyForm);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const saveMutation = useMutation({
    mutationFn: async (payload: Partial<Seguro>) => {
      if (editingSeguro) {
        return (await api.put<Seguro>(`/seguro/${editingSeguro.id}`, payload)).data;
      }
      return (await api.post<Seguro>('/seguro', payload)).data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['seguros'] });
      setIsDialogOpen(false);
      setEditingSeguro(null);
      setFormValues(emptyForm);
      setErrorMessage(null);
    },
    onError: (error) => setErrorMessage(getErrorMessage(error)),
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: string) => api.delete(`/seguro/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['seguros'] });
    },
    onError: (error) => setErrorMessage(getErrorMessage(error)),
  });

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!formValues.tipo) {
      setErrorMessage('Selecione o tipo do seguro.');
      return;
    }
    saveMutation.mutate({
      titulo: formValues.titulo,
      tipo: formValues.tipo,
      coberturaMinima: Number(formValues.coberturaMinima),
      valorPremioBase: Number(formValues.valorPremioBase),
    });
  };

  return (
    <Stack spacing={3}>
      <SectionHeader
        title="Produtos de seguro"
        subtitle="Cadastre produtos, defina cobertura mínima e prêmio base para agilizar a cotação."
        action={
          <Button
            variant="contained"
            startIcon={<AddRoundedIcon />}
            onClick={() => {
              setEditingSeguro(null);
              setFormValues(emptyForm);
              setIsDialogOpen(true);
            }}
          >
            Novo seguro
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
                  <TableCell>Produto</TableCell>
                  <TableCell>Tipo</TableCell>
                  <TableCell>Cobertura mínima</TableCell>
                  <TableCell>Prêmio base</TableCell>
                  <TableCell align="center" width={140}>
                    Ações
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {seguros?.map((seguro) => (
                  <TableRow key={seguro.id}>
                    <TableCell>{seguro.titulo}</TableCell>
                    <TableCell>{seguro.tipo}</TableCell>
                    <TableCell>{formatCurrency(seguro.coberturaMinima)}</TableCell>
                    <TableCell>{formatCurrency(seguro.valorPremioBase)}</TableCell>
                    <TableCell align="center">
                      <Tooltip title="Editar">
                        <IconButton
                          size="small"
                          color="primary"
                          onClick={() => {
                            setEditingSeguro(seguro);
                            setFormValues({
                              titulo: seguro.titulo,
                              tipo: seguro.tipo,
                              coberturaMinima: String(seguro.coberturaMinima ?? ''),
                              valorPremioBase: String(seguro.valorPremioBase ?? ''),
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
                          onClick={() => deleteMutation.mutate(seguro.id)}
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
        title={editingSeguro ? 'Editar produto' : 'Novo produto'}
        onClose={() => {
          setIsDialogOpen(false);
          setEditingSeguro(null);
        }}
        onSubmit={handleSubmit}
        isSubmitting={saveMutation.isPending}
      >
        <TextField
          label="Título do seguro"
          value={formValues.titulo}
          required
          onChange={(event) => setFormValues((prev) => ({ ...prev, titulo: event.target.value }))}
        />
        <FormControl fullWidth>
          <InputLabel id="tipo-seguro-label">Tipo</InputLabel>
          <Select
            labelId="tipo-seguro-label"
            label="Tipo"
            value={formValues.tipo}
            onChange={(event) => setFormValues((prev) => ({ ...prev, tipo: event.target.value as TipoSeguro }))}
            required
          >
            {tipoOptions.map((option) => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Cobertura mínima"
            type="number"
            value={formValues.coberturaMinima}
            required
            onChange={(event) => setFormValues((prev) => ({ ...prev, coberturaMinima: event.target.value }))}
            fullWidth
          />
          <TextField
            label="Prêmio base"
            type="number"
            value={formValues.valorPremioBase}
            required
            onChange={(event) => setFormValues((prev) => ({ ...prev, valorPremioBase: event.target.value }))}
            fullWidth
          />
        </Stack>
      </EntityDialog>
    </Stack>
  );
};

export default SegurosPage;
