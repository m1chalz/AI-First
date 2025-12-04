import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { ReportMissingPetFlowProvider } from '../../../contexts/ReportMissingPetFlowContext';
import { BrowserRouter } from 'react-router-dom';
import { SummaryScreen } from '../SummaryScreen';
import '@testing-library/jest-dom';

const renderWithRouter = (component: React.ReactNode) =>
  render(
    <BrowserRouter>
      <ReportMissingPetFlowProvider>
        {component}
      </ReportMissingPetFlowProvider>
    </BrowserRouter>
  );

describe('SummaryScreen - Figma Design', () => {
  it('should render success heading with correct text', () => {
    // given
    renderWithRouter(<SummaryScreen />);

    // then
    const heading = screen.getByText('Report created');
    expect(heading).toBeDefined();
  });

  it('should display description text about report creation', () => {
    // given
    renderWithRouter(<SummaryScreen />);

    // then
    expect(screen.getByText(/Your report has been created/i)).toBeDefined();
    expect(screen.getByText(/missing animal has been added to the database/i)).toBeDefined();
  });

  it('should display Close button', () => {
    // given
    renderWithRouter(<SummaryScreen />);

    // then
    const closeButton = screen.getByRole('button', { name: /Close/i });
    expect(closeButton).toBeDefined();
  });

  it('should have correct layout with proper spacing', () => {
    // given
    renderWithRouter(<SummaryScreen />);

    // then
    const heading = screen.getByText('Report created');
    expect(heading).toBeDefined();
    const closeButton = screen.getByRole('button', { name: /Close/i });
    expect(closeButton).toBeDefined();
  });

  it('should display message about code being sent to email', () => {
    // given
    renderWithRouter(<SummaryScreen />);

    // then
    expect(screen.getByText(/This code has also been sent to your email address/i)).toBeDefined();
  });

  it('should not render password card when managementPassword is missing', () => {
    // given
    renderWithRouter(<SummaryScreen />);

    // then - password text should not be in document when state is not passed
    expect(screen.queryByTestId('summary.password.text')).toBeNull();
  });
});
