import { NotFoundError } from '../lib/errors.ts';

export default function notFoundMiddleware() {
  throw new NotFoundError();
}

