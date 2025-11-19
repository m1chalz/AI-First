import server from './server.ts';
import log from './lib/logger.ts';

const port = 3000
server.listen(port, (err) => !err ? log.info(`Server running on port ${port}`) : log.error(err))
