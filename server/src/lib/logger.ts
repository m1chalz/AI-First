import { pino } from 'pino';

const log = pino({
  level: process.env.NODE_ENV === 'test' ? 'warn' : 'info',
  timestamp: pino.stdTimeFunctions.isoTime,
  formatters: {
    level: (label: string) => ({ level: label.toUpperCase() }),
  },
});

export default log;
