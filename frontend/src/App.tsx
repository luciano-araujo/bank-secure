import { Box, Container } from '@mui/material';
import { Navigate, Outlet, Route, Routes } from 'react-router-dom';
import AppHeader from './components/AppHeader';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import ClientesPage from './pages/ClientesPage';
import SegurosPage from './pages/SegurosPage';
import ApolicesPage from './pages/ApolicesPage';
import BensPage from './pages/BensPage';
import CotacoesPage from './pages/CotacoesPage';

const AppLayout = () => (
  <Box
    sx={{
      minHeight: '100vh',
      background:
        'radial-gradient(circle at 10% 20%, rgba(82,224,199,0.08), transparent 25%), radial-gradient(circle at 80% 0%, rgba(79,155,255,0.25), transparent 45%), #030712',
      pb: 8,
    }}
  >
    <AppHeader />
    <Container maxWidth="xl" sx={{ py: 5 }}>
      <Outlet />
    </Container>
  </Box>
);

const App = () => (
  <Routes>
    <Route path="/login" element={<LoginPage />} />
    <Route
      element={
        <ProtectedRoute>
          <AppLayout />
        </ProtectedRoute>
      }
    >
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/clientes" element={<ClientesPage />} />
      <Route path="/seguros" element={<SegurosPage />} />
      <Route path="/apolices" element={<ApolicesPage />} />
      <Route path="/bens" element={<BensPage />} />
      <Route path="/cotacoes" element={<CotacoesPage />} />
    </Route>
    <Route path="*" element={<Navigate to="/dashboard" replace />} />
  </Routes>
);

export default App;
