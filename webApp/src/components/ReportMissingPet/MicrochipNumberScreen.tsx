import { useNavigate } from 'react-router-dom';
import { MicrochipNumberContent } from './MicrochipNumberContent';
import { useMicrochipFormatter } from '../../hooks/use-microchip-formatter';
import { useReportMissingPetFlow } from '../../hooks/use-report-missing-pet-flow';
import { FlowStep } from '../../models/ReportMissingPetFlow';

export function MicrochipNumberScreen() {
  const navigate = useNavigate();
  const { value, formattedValue, handleChange, handlePaste } = useMicrochipFormatter();
  const { updateFlowState, clearFlowState } = useReportMissingPetFlow();

  const handleContinue = () => {
    updateFlowState({
      microchipNumber: value,
      currentStep: FlowStep.Photo,
    });
    navigate('/report-missing/photo');
  };

  const handleBack = () => {
    clearFlowState();
    navigate('/');
  };

  return (
    <MicrochipNumberContent
      formattedValue={formattedValue}
      onMicrochipChange={handleChange}
      onMicrochipPaste={handlePaste}
      onContinue={handleContinue}
      onBack={handleBack}
    />
  );
}

