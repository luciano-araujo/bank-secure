import { Box, CircularProgress } from '@mui/material';

const LoadingState = () => (
  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', py: 6 }}>
    <CircularProgress color="primary" />
  </Box>
);

export default LoadingState;

