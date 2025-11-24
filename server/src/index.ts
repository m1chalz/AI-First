import type { Server } from 'node:http';
import log from './lib/logger.ts';
import server from './server.ts';

const port = Number(process.env.PORT ?? 3000);
const httpServer: Server = server.listen(port, () => log.info(`Server running on port ${port}`));
httpServer.on('error', (error) => {
  log.error(error, 'HTTP server error');
  process.exit(1);
});

function cleanup(signal: NodeJS.Signals): void {
  log.info(`${signal} received, closing server gracefully...`);
  httpServer.close((err) => {
    if (err) {
      log.error(err, 'Error during server shutdown');
      process.exit(1);
    }
    log.info('Server closed.');
    process.exit(0);
  });

  setTimeout(() => {
    log.error('Forced shutdown after timeout');
    process.exit(1);
  }, 10000);
}

process.on('SIGTERM', () => cleanup('SIGTERM'));
process.on('SIGINT', () => cleanup('SIGINT'));
