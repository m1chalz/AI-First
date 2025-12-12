import multer from 'multer';

const CONFIG = {
  storage: multer.memoryStorage(),
  limits: {
    fileSize: 20 * 1024 * 1024 // 20 MB
  }
};

export default multer(CONFIG);
