import { alpha, createTheme } from '@mui/material/styles';

const background = '#040b1a';
const paper = '#0f1b34';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#4f9bff',
    },
    secondary: {
      main: '#52e0c7',
    },
    background: {
      default: background,
      paper,
    },
  },
  shape: {
    borderRadius: 18,
  },
  typography: {
    fontFamily: '"Inter", "Segoe UI", system-ui, -apple-system, BlinkMacSystemFont, sans-serif',
    h4: {
      fontWeight: 600,
    },
    h5: {
      fontWeight: 600,
    },
  },
  components: {
    MuiAppBar: {
      defaultProps: {
        elevation: 0,
        color: 'transparent',
      },
      styleOverrides: {
        root: {
          backgroundImage: 'none',
          backgroundColor: alpha('#050b19', 0.95),
          borderBottom: `1px solid ${alpha('#ffffff', 0.05)}`,
          backdropFilter: 'blur(16px)',
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundColor: paper,
          backgroundImage: 'linear-gradient(135deg, rgba(79,155,255,0.08), rgba(82,224,199,0.05))',
          border: `1px solid ${alpha('#ffffff', 0.04)}`,
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          textTransform: 'none',
          fontWeight: 600,
        },
      },
    },
  },
});

export default theme;

