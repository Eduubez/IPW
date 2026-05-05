import CircularProgress from '@mui/material/CircularProgress';
import Box from '@mui/material/Box';

export default function LoadingComponent() {
  return (
    <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '1rem' }}>
      <CircularProgress aria-label="Loading…" sx={{ color: 'var(--color-light-blue)' }} />
    </Box>
  );
}