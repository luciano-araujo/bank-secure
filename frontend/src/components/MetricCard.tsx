import { Paper, Stack, Typography } from '@mui/material';
import type { ReactNode } from 'react';

type MetricCardProps = {
  label: string;
  value: string;
  icon: ReactNode;
  helperText?: string;
  loading?: boolean;
};

const MetricCard = ({ label, value, icon, helperText, loading }: MetricCardProps) => (
  <Paper
    elevation={0}
    sx={{
      p: 3,
      height: '100%',
      borderRadius: 4,
      display: 'flex',
      flexDirection: 'column',
      gap: 1.5,
    }}
  >
    <Stack direction="row" alignItems="center" justifyContent="space-between">
      <Typography variant="subtitle2" color="text.secondary">
        {label}
      </Typography>
      <Stack
        sx={{
          width: 46,
          height: 46,
          borderRadius: '16px',
          backgroundColor: 'rgba(255,255,255,0.06)',
          alignItems: 'center',
          justifyContent: 'center',
          display: 'flex',
          color: 'primary.main',
        }}
      >
        {icon}
      </Stack>
    </Stack>
    <Typography variant="h4" fontWeight={700} sx={{ minHeight: 44 }}>
      {loading ? '...' : value}
    </Typography>
    {helperText && (
      <Typography variant="body2" color="text.secondary">
        {helperText}
      </Typography>
    )}
  </Paper>
);

export default MetricCard;

