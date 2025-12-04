import { describe, it, expect } from 'vitest';
import { initialFlowState, FlowStep } from '../ReportMissingPetFlow';

describe('ReportMissingPetFlow', () => {
  // given
  describe('initialFlowState', () => {
    it('should have empty contact fields', () => {
      // when
      const state = initialFlowState;

      // then
      expect(state.phone).toBe('');
      expect(state.email).toBe('');
      expect(state.reward).toBe('');
    });

    it('should have correct initial step', () => {
      // when
      const state = initialFlowState;

      // then
      expect(state.currentStep).toBe(FlowStep.Empty);
    });

    it('should have all required fields', () => {
      // when
      const state = initialFlowState;

      // then
      expect(state).toHaveProperty('phone');
      expect(state).toHaveProperty('email');
      expect(state).toHaveProperty('reward');
      expect(state).toHaveProperty('microchipNumber');
      expect(state).toHaveProperty('photo');
      expect(state).toHaveProperty('lastSeenDate');
      expect(state).toHaveProperty('species');
      expect(state).toHaveProperty('breed');
      expect(state).toHaveProperty('sex');
      expect(state).toHaveProperty('age');
      expect(state).toHaveProperty('description');
      expect(state).toHaveProperty('latitude');
      expect(state).toHaveProperty('longitude');
    });
  });
});

