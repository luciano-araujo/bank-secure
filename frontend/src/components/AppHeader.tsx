import { useMemo } from 'react';
import { AppBar, Avatar, Box, Button, Stack, Toolbar, Typography } from '@mui/material';
import DashboardRoundedIcon from '@mui/icons-material/DashboardRounded';
import PeopleRoundedIcon from '@mui/icons-material/PeopleRounded';
import ShieldRoundedIcon from '@mui/icons-material/ShieldRounded';
import AssignmentTurnedInRoundedIcon from '@mui/icons-material/AssignmentTurnedInRounded';
import InventoryRoundedIcon from '@mui/icons-material/InventoryRounded';
import CalculateRoundedIcon from '@mui/icons-material/CalculateRounded';
import { NavLink, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const navItems = [
  { label: 'Dashboard', to: '/dashboard', icon: <DashboardRoundedIcon fontSize="small" /> },
  { label: 'Clientes', to: '/clientes', icon: <PeopleRoundedIcon fontSize="small" /> },
  { label: 'Seguros', to: '/seguros', icon: <ShieldRoundedIcon fontSize="small" /> },
  { label: 'Apólices', to: '/apolices', icon: <AssignmentTurnedInRoundedIcon fontSize="small" /> },
  { label: 'Bens', to: '/bens', icon: <InventoryRoundedIcon fontSize="small" /> },
  { label: 'Cotações', to: '/cotacoes', icon: <CalculateRoundedIcon fontSize="small" /> },
];

const AppHeader = () => {
  const location = useLocation();
  const { user, logout } = useAuth();

  const activePath = useMemo(() => {
    const current = navItems.find((item) => location.pathname.startsWith(item.to));
    return current?.to ?? '/dashboard';
  }, [location.pathname]);

  return (
    <AppBar position="sticky">
      <Toolbar sx={{ justifyContent: 'space-between', gap: 2, py: 2 }}>
        <Stack direction="row" alignItems="center" spacing={1.5}>
          <Box
            sx={{
              width: 40,
              height: 40,
              borderRadius: '14px',
              background: 'linear-gradient(135deg, #4f9bff 0%, #52e0c7 100%)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 700,
              letterSpacing: 1,
            }}
          >
            BS
          </Box>
          <Stack spacing={0.2}>
            <Typography variant="h6" fontWeight={700}>
              BankSecure
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Gestão inteligente de seguros
            </Typography>
          </Stack>
        </Stack>

        <Stack direction="row" spacing={1} sx={{ overflowX: 'auto' }}>
          {navItems.map((item) => {
            const isActive = activePath === item.to;
            return (
              <Button
                key={item.to}
                component={NavLink}
                to={item.to}
                startIcon={item.icon}
                size="small"
                variant={isActive ? 'contained' : 'text'}
                sx={{
                  color: isActive ? 'common.white' : 'text.secondary',
                  backgroundColor: isActive ? 'primary.main' : 'transparent',
                  borderRadius: 999,
                  px: 2.5,
                }}
              >
                {item.label}
              </Button>
            );
          })}
        </Stack>
        {user && (
          <Stack direction="row" spacing={1.5} alignItems="center">
            <Stack spacing={0} textAlign="right">
              <Typography variant="subtitle2" fontWeight={600}>
                {user.nome}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {user.tipoUsuario}
              </Typography>
            </Stack>
            <Avatar
              sx={{
                bgcolor: 'secondary.main',
                color: 'grey.900',
                width: 40,
                height: 40,
                fontWeight: 700,
              }}
            >
              {user.nome
                .split(' ')
                .map((part) => part[0])
                .join('')
                .slice(0, 2)
                .toUpperCase()}
            </Avatar>
            <Button variant="outlined" color="inherit" size="small" onClick={logout}>
              Sair
            </Button>
          </Stack>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default AppHeader;
