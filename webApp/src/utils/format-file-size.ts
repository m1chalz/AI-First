export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 bytes';
  
  const kilobyte = 1024;
  const megabyte = kilobyte * 1024;
  
  if (bytes >= megabyte) {
    return `${(bytes / megabyte).toFixed(1)} MB`;
  }
  
  if (bytes >= kilobyte) {
    return `${(bytes / kilobyte).toFixed(1)} KB`;
  }
  
  return `${bytes} bytes`;
}

