import xss from 'xss';

const CONFIG = {
  whiteList: {},
  stripIgnoreTag: true,
  stripIgnoreTagBody: ['script', 'style']
};

export default function sanitizeText(input: string): string {
  return xss(input, CONFIG);
}
