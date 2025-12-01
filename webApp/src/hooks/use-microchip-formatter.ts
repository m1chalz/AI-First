import { useState, useEffect } from 'react';
import { formatMicrochip, stripNonDigits } from '../utils/microchip-formatter';
import { UseMicrochipFormatterReturn } from '../models/ReportMissingPetFlow';

export function useMicrochipFormatter(initialValue = ''): UseMicrochipFormatterReturn {
  const [value, setValue] = useState(initialValue);
  const [formattedValue, setFormattedValue] = useState(formatMicrochip(initialValue));

  useEffect(() => {
    setValue(initialValue);
    setFormattedValue(formatMicrochip(initialValue));
  }, [initialValue]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const input = e.target.value;
    const digitsOnly = stripNonDigits(input).slice(0, 15);
    setValue(digitsOnly);
    setFormattedValue(formatMicrochip(digitsOnly));
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const pastedText = e.clipboardData.getData('text');
    const digitsOnly = stripNonDigits(pastedText).slice(0, 15);
    setValue(digitsOnly);
    setFormattedValue(formatMicrochip(digitsOnly));
  };

  const reset = () => {
    setValue('');
    setFormattedValue('');
  };

  return {
    value,
    formattedValue,
    handleChange,
    handlePaste,
    reset,
  };
}

