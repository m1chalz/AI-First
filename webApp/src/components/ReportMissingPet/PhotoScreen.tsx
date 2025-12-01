import { useReportMissingPetFlow } from '../../hooks/use-report-missing-pet-flow';

export function PhotoScreen() {
  const { flowState } = useReportMissingPetFlow();

  return (
    <div style={{ padding: '20px' }}>
      <h1>Photo Step - Coming Soon</h1>
      <p>This is step 2/4 of the flow</p>
      <p>
        <strong>Flow State:</strong>
      </p>
      <pre>{JSON.stringify(flowState, null, 2)}</pre>
    </div>
  );
}

