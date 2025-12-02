import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useToast } from '../use-toast';

describe('use-toast', () => {
  beforeEach(() => {
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.restoreAllMocks();
    vi.useRealTimers();
  });

  describe('initial state', () => {
    it('initializes with null message', () => {
      // given / when
      const { result } = renderHook(() => useToast());

      // then
      expect(result.current.message).toBe(null);
    });
  });

  describe('showToast', () => {
    it('sets message', () => {
      // given
      const { result } = renderHook(() => useToast());

      // when
      act(() => result.current.showToast('Test message', 3000));

      // then
      expect(result.current.message).toBe('Test message');
    });

    it('clears message after duration', () => {
      // given
      const { result } = renderHook(() => useToast());

      act(() => result.current.showToast('Test message', 3000));

      // when
      act(() => vi.advanceTimersByTime(3000));

      // then
      expect(result.current.message).toBe(null);
    });

    it('replaces message when called multiple times rapidly', () => {
      // given
      const { result } = renderHook(() => useToast());

      // when
      act(() => result.current.showToast('First message', 3000));
      act(() => result.current.showToast('Second message', 3000));

      // then
      expect(result.current.message).toBe('Second message');
    });

    it('clears previous timeout when new message shown', () => {
      // given
      const { result } = renderHook(() => useToast());

      act(() => result.current.showToast('First message', 3000));
      act(() => vi.advanceTimersByTime(1000));
      act(() => result.current.showToast('Second message', 3000));
      act(() => vi.advanceTimersByTime(2000));

      // then
      expect(result.current.message).toBe('Second message');

      act(() => vi.advanceTimersByTime(1000));

      expect(result.current.message).toBe(null);
    });

    it('uses default duration when not provided', () => {
      // given
      const { result } = renderHook(() => useToast());

      // when
      act(() => result.current.showToast('Test message'));
      act(() => vi.advanceTimersByTime(2999));

      // then
      expect(result.current.message).toBe('Test message');

      act(() => vi.advanceTimersByTime(1));

      expect(result.current.message).toBe(null);
    });
  });

  describe('clearToast', () => {
    it('immediately clears message', () => {
      // given
      const { result } = renderHook(() => useToast());

      act(() => result.current.showToast('Test message', 3000));

      // when
      act(() => result.current.clearToast());

      // then
      expect(result.current.message).toBe(null);
    });

    it('cancels pending timeout', () => {
      // given
      const { result } = renderHook(() => useToast());

      act(() => result.current.showToast('Test message', 3000));
      act(() => result.current.clearToast());

      // when
      act(() => vi.advanceTimersByTime(3000));

      // then
      expect(result.current.message).toBe(null);
    });

    it('does nothing when message is already null', () => {
      // given
      const { result } = renderHook(() => useToast());

      // when
      act(() => result.current.clearToast());

      // then
      expect(result.current.message).toBe(null);
    });
  });
});

