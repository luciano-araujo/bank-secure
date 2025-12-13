import { useState } from 'react';
import {
  Alert,
  IconButton,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Tooltip,
  Button,
} from '@mui/material';
import AddRoundedIcon from '@mui/icons-material/AddRounded';
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import DeleteRoundedIcon from '@mui/icons-material/DeleteRounded';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import SectionHeader from '../components/SectionHeader';
import EntityDialog from '../components/EntityDialog';
import LoadingState from '../components/LoadingState';
import api from '../services/api';
import { fetchClientes } from '../services/endpoints';
import type { Cliente } from '../types';
import { normalizeDateInput } from '../utils/formatters';
import { getErrorMessage } from '../utils/errorMessage';

const emptyForm: Partial<Cliente> = {
  nome: '',
  email: '',
  cpf: '',
  telefone: '',
  senha: '',
  dataNascimento: '',
};

const ClientesPage = () => {
  const queryClient = useQueryClient();
  const { data: clientes, isLoading } = useQuery<Cliente[]>({
    queryKey: ['clientes'],
    queryFn: fetchClientes,
  });

  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingCliente, setEditingCliente] = useState<Cliente | null>(null);
  const [formValues, setFormValues] = useState<Partial<Cliente>>(emptyForm);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const saveMutation = useMutation({
    mutationFn: async (payload: Partial<Cliente>) => {
      if (editingCliente) {
        const serialized = { ...payload };
        if (!serialized.senha) {
          delete serialized.senha;
        }
        return (await api.put<Cliente>(`/cliente/${editingCliente.id}`, serialized)).data;
      }

      return (await api.post<Cliente>('/cliente', payload)).data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes'] });
      setIsDialogOpen(false);
      setEditingCliente(null);
      setFormValues(emptyForm);
      setErrorMessage(null);
    },
    onError: (error) => {
      setErrorMessage(getErrorMessage(error));
    },
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/cliente/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['clientes'] });
    },
    onError: (error) => {
      setErrorMessage(getErrorMessage(error));
    },
  });

  const handleOpenCreate = () => {
    setEditingCliente(null);
    setFormValues(emptyForm);
    setIsDialogOpen(true);
  };

  const handleEdit = (cliente: Cliente) => {
    setEditingCliente(cliente);
    setFormValues({
      ...cliente,
      senha: '',
      dataNascimento: normalizeDateInput(cliente.dataNascimento),
    });
    setIsDialogOpen(true);
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    saveMutation.mutate(formValues);
  };

  return (
    <Stack spacing={3}>
      <SectionHeader
        title="Clientes"
        subtitle="Cadastre, atualize e gerencie a base de clientes elegíveis para os produtos de seguro."
        action={
          <Button variant="contained" startIcon={<AddRoundedIcon />} onClick={handleOpenCreate}>
            Novo cliente
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
                  <TableCell>Nome</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>CPF</TableCell>
                  <TableCell>Telefone</TableCell>
                  <TableCell width={120} align="center">
                    Ações
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {clientes?.map((cliente) => (
                  <TableRow key={cliente.id}>
                    <TableCell>{cliente.nome}</TableCell>
                    <TableCell>{cliente.email}</TableCell>
                    <TableCell>{cliente.cpf}</TableCell>
                    <TableCell>{cliente.telefone}</TableCell>
                    <TableCell align="center">
                      <Tooltip title="Editar">
                        <IconButton color="primary" size="small" onClick={() => handleEdit(cliente)}>
                          <EditRoundedIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      <Tooltip title="Excluir">
                        <IconButton
                          color="error"
                          size="small"
                          onClick={() => deleteMutation.mutate(cliente.id)}
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
        title={editingCliente ? 'Atualizar cliente' : 'Novo cliente'}
        onClose={() => {
          setIsDialogOpen(false);
          setEditingCliente(null);
        }}
        onSubmit={handleSubmit}
        isSubmitting={saveMutation.isPending}
      >
        <TextField
          label="Nome completo"
          value={formValues.nome ?? ''}
          onChange={(event) => setFormValues((prev) => ({ ...prev, nome: event.target.value }))}
          required
          fullWidth
        />
        <TextField
          label="E-mail"
          type="email"
          value={formValues.email ?? ''}
          onChange={(event) => setFormValues((prev) => ({ ...prev, email: event.target.value }))}
          required
          fullWidth
        />
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="CPF"
            value={formValues.cpf ?? ''}
            onChange={(event) => setFormValues((prev) => ({ ...prev, cpf: event.target.value }))}
            required
            fullWidth
          />
          <TextField
            label="Telefone"
            value={formValues.telefone ?? ''}
            onChange={(event) => setFormValues((prev) => ({ ...prev, telefone: event.target.value }))}
            required
            fullWidth
          />
        </Stack>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <TextField
            label="Data de nascimento"
            type="date"
            InputLabelProps={{ shrink: true }}
            value={formValues.dataNascimento ?? ''}
            onChange={(event) => setFormValues((prev) => ({ ...prev, dataNascimento: event.target.value }))}
            required
            fullWidth
          />
          <TextField
            label="Senha"
            type="password"
            value={formValues.senha ?? ''}
            onChange={(event) => setFormValues((prev) => ({ ...prev, senha: event.target.value }))}
            required={!editingCliente}
            fullWidth
            helperText={editingCliente ? 'Preencha para redefinir a senha.' : undefined}
          />
        </Stack>
      </EntityDialog>
    </Stack>
  );
};

export default ClientesPage;

