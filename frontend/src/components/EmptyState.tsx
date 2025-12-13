import { Paper, Stack, Typography } from '@mui/material';
import type { ReactNode } from 'react';

type EmptyStateProps = {
  title: string;
  description?: string;
  action?: ReactNode;
};

const EmptyState = ({ title, description, action }: EmptyStateProps) => (
  <Paper
    sx={{
      p: 4,
      textAlign: 'center',
      borderRadius: 4,
    }}
  >
    <Stack spacing={1.5} alignItems="center">
      <Typography variant="h6">{title}</Typography>
      {description && (
        <Typography variant="body2" color="text.secondary" maxWidth={400}>
          {description}
        </Typography>
      )}
      {action}
    </Stack>
  </Paper>
);

export default EmptyState;

