import { useState } from 'react';
import {
  Alert,
  Box,
  Button,
  Chip,
  Container,
  Divider,
  IconButton,
  InputAdornment,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import VisibilityRoundedIcon from "@mui/icons-material/VisibilityRounded";
import VisibilityOffRoundedIcon from "@mui/icons-material/VisibilityOffRounded";
import { useNavigate, useLocation } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { useAuth } from "../context/AuthContext";
import { fetchSeguros } from "../services/endpoints";
import type { Seguro } from "../types";
import { formatCurrency } from "../utils/formatters";
import LoadingState from "../components/LoadingState";

const LoginPage = () => {
  const { login, error, clearError } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { data: seguros, isLoading: isLoadingSeguros } = useQuery<Seguro[]>({
    queryKey: ["public-seguros"],
    queryFn: fetchSeguros,
  });
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const redirectTo =
    (location.state as { from?: string })?.from ?? "/dashboard";

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    try {
      await login({ email, senha });
      navigate(redirectTo, { replace: true });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background:
          "radial-gradient(circle at 20% 20%, rgba(82,224,199,0.2), transparent 35%), radial-gradient(circle at 80% 0%, rgba(79,155,255,0.3), transparent 45%), #030712",
        py: 6,
        px: 2,
      }}
    >
      <Container maxWidth="md">
        <Paper
          sx={{
            p: 5,
            borderRadius: 5,
            backgroundColor: "rgba(11, 16, 35, 0.9)",
            border: "1px solid rgba(255,255,255,0.05)",
            backdropFilter: "blur(20px)",
          }}
        >
          <Stack direction={{ xs: "column", md: "row" }} spacing={4}>
            <Stack
              spacing={4}
              flex={1}
              component="form"
              onSubmit={handleSubmit}
            >
              <Stack spacing={1.5}>
                <Typography variant="h4" fontWeight={700}>
                  Bem-vindo de volta
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Faça login com seu e-mail corporativo para acessar o cockpit
                  BankSecure.
                </Typography>
              </Stack>

              {error && (
                <Alert severity="error" onClose={clearError}>
                  {error}
                </Alert>
              )}

              <TextField
                label="E-mail"
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                required
                fullWidth
                autoComplete="username"
              />
              <TextField
                label="Senha"
                type={showPassword ? "text" : "password"}
                value={senha}
                onChange={(event) => setSenha(event.target.value)}
                required
                fullWidth
                autoComplete="current-password"
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        onClick={() => setShowPassword((prev) => !prev)}
                        edge="end"
                      >
                        {showPassword ? (
                          <VisibilityOffRoundedIcon />
                        ) : (
                          <VisibilityRoundedIcon />
                        )}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />
              <Button
                type="submit"
                variant="contained"
                size="large"
                disabled={submitting}
              >
                {submitting ? "Validando..." : "Entrar"}
              </Button>
            </Stack>

            <Divider
              orientation="vertical"
              flexItem
              sx={{ display: { xs: "none", md: "block" }, opacity: 0.2 }}
            />

            <Stack spacing={2} flex={1}>
              <Stack spacing={0.5}>
                <Typography variant="h5" fontWeight={700}>
                  Seguros disponíveis
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Clientes podem simular os produtos antes do login. Consulte os
                  detalhes abaixo.
                </Typography>
              </Stack>
              {isLoadingSeguros ? (
                <LoadingState />
              ) : (
                <Stack spacing={2}>
                  {seguros && seguros.length > 0 ? (
                    seguros.map((seguro) => (
                      <Box
                        key={seguro.id}
                        sx={{
                          borderRadius: 3,
                          border: "1px solid rgba(255,255,255,0.07)",
                          p: 2.5,
                          backgroundColor: "rgba(255,255,255,0.02)",
                        }}
                      >
                        <Stack spacing={0.5}>
                          <Stack
                            direction="row"
                            spacing={1}
                            alignItems="center"
                          >
                            <Typography variant="subtitle1" fontWeight={600}>
                              {seguro.titulo}
                            </Typography>
                            <Chip
                              label={seguro.tipo}
                              size="small"
                              color="secondary"
                            />
                          </Stack>
                          <Typography variant="body2" color="text.secondary">
                            Cobertura mínima:{" "}
                            {formatCurrency(seguro.coberturaMinima)}
                          </Typography>
                          <Typography variant="body2" color="text.secondary">
                            Prêmio base:{" "}
                            {formatCurrency(seguro.valorPremioBase)}
                          </Typography>
                        </Stack>
                      </Box>
                    ))
                  ) : (
                    <Typography variant="body2" color="text.secondary">
                      Nenhum seguro cadastrado no momento.
                    </Typography>
                  )}
                </Stack>
              )}
            </Stack>
          </Stack>
        </Paper>
      </Container>
    </Box>
  );
};

export default LoginPage;
