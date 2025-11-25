export function redactPhone(phone: string): string {
  if (phone.length < 3) {
    return phone;
  }
  const lastThree = phone.slice(-3);
  return '***-***-' + lastThree;
}

export function redactEmail(email: string): string {
  const parts = email.split('@');
  if (parts.length !== 2) {
    return email;
  }
  
  const [localPart, domain] = parts;
  if (!localPart || !domain) {
    return email;
  }
  
  const firstChar = localPart[0];
  return `${firstChar}***@${domain}`;
}


