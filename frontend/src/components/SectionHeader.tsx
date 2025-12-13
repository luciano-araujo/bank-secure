import { Stack, Typography } from '@mui/material';

type SectionHeaderProps = {
  title: string;
  subtitle?: string;
  action?: React.ReactNode;
};

const SectionHeader = ({ title, subtitle, action }: SectionHeaderProps) => (
  <Stack
    direction="row"
    alignItems={{ xs: 'flex-start', md: 'center' }}
    justifyContent="space-between"
    spacing={2}
    flexWrap="wrap"
  >
    <Stack spacing={0.5}>
      <Typography variant="h5">{title}</Typography>
      {subtitle && (
        <Typography variant="body2" color="text.secondary">
          {subtitle}
        </Typography>
      )}
    </Stack>
    {action}
  </Stack>
);

export default SectionHeader;

