import { Header } from './Header';
import styles from './MicrochipNumberContent.module.css';

interface MicrochipNumberContentProps {
  formattedValue: string;
  onMicrochipChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onMicrochipPaste: (e: React.ClipboardEvent<HTMLInputElement>) => void;
  onContinue: () => void;
  onBack: () => void;
}

export function MicrochipNumberContent({
  formattedValue,
  onMicrochipChange,
  onMicrochipPaste,
  onContinue,
  onBack,
}: MicrochipNumberContentProps) {
  return (
    <div className={styles.container}>
      <Header title="Microchip number" progress="1/4" onBack={onBack} />
      
      <div className={styles.content}>
        <h2 className={styles.heading}>Identification by Microchip</h2>
        
        <p className={styles.description}>
          Microchip identification is the most efficient way to reunite with your pet. 
          If your pet has been microchipped and you know the microchip number, please enter it here.
        </p>
        
        <div className={styles.inputGroup}>
          <label htmlFor="microchip-input" className={styles.label}>
            Microchip number (optional)
          </label>
          <input
            id="microchip-input"
            type="text"
            value={formattedValue}
            onChange={onMicrochipChange}
            onPaste={onMicrochipPaste}
            placeholder="00000-00000-00000"
            className={styles.input}
            data-testid="reportMissingPet.step1.microchipInput.field"
          />
        </div>
        
        <button
          onClick={onContinue}
          className={styles.continueButton}
          data-testid="reportMissingPet.step1.continueButton.click"
        >
          Continue
        </button>
      </div>
    </div>
  );
}

