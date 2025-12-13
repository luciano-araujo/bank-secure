import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack } from '@mui/material';
import type { DialogProps } from '@mui/material';
import type { ReactNode } from 'react';

type EntityDialogProps = {
  open: boolean;
  title: string;
  onClose: () => void;
  onSubmit: (event: React.FormEvent<HTMLFormElement>) => void;
  isSubmitting?: boolean;
  submitLabel?: string;
  children: ReactNode;
  maxWidth?: DialogProps['maxWidth'];
};

const EntityDialog = ({
  open,
  title,
  onClose,
  onSubmit,
  children,
  isSubmitting,
  submitLabel = 'Salvar',
  maxWidth = 'sm',
}: EntityDialogProps) => (
  <Dialog open={open} onClose={onClose} maxWidth={maxWidth} fullWidth>
    <Box component="form" onSubmit={onSubmit}>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent dividers>
        <Stack spacing={2} py={1}>
          {children}
        </Stack>
      </DialogContent>
      <DialogActions sx={{ px: 3, py: 2 }}>
        <Button onClick={onClose} color="inherit">
          Cancelar
        </Button>
        <Button type="submit" variant="contained" disabled={isSubmitting}>
          {isSubmitting ? 'Salvando...' : submitLabel}
        </Button>
      </DialogActions>
    </Box>
  </Dialog>
);

export default EntityDialog;
