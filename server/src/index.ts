import prepareApp from "./app.ts";
import log from './lib/logger.ts';

const app = await prepareApp();

const port = 3000
app.listen(port, (err) => !err ? log.info(`Server running on port ${port}`) : log.error(err))
