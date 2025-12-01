import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { renderHook } from '@testing-library/react';
import { useBrowserBackHandler } from '../use-browser-back-handler';

describe('useBrowserBackHandler', () => {
  let popstateListeners: ((event: PopStateEvent) => void)[] = [];

  beforeEach(() => {
    popstateListeners = [];
    
    // Mock addEventListener to capture listeners
    vi.spyOn(window, 'addEventListener').mockImplementation((event: string, listener: EventListenerOrEventListenerObject) => {
      if (event === 'popstate') {
        popstateListeners.push(listener as (event: PopStateEvent) => void);
      }
    });

    // Mock removeEventListener to remove listeners
    vi.spyOn(window, 'removeEventListener').mockImplementation((event: string, listener: EventListenerOrEventListenerObject) => {
      if (event === 'popstate') {
        const index = popstateListeners.indexOf(listener as (event: PopStateEvent) => void);
        if (index > -1) {
          popstateListeners.splice(index, 1);
        }
      }
    });
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('registers popstate event listener on mount', () => {
    // given
    const onBack = vi.fn();

    // when
    renderHook(() => useBrowserBackHandler(onBack));

    // then
    expect(window.addEventListener).toHaveBeenCalledWith('popstate', expect.any(Function));
    expect(popstateListeners.length).toBe(1);
  });

  it('calls onBack callback when browser back button pressed', () => {
    // given
    const onBack = vi.fn();
    renderHook(() => useBrowserBackHandler(onBack));

    // when (simulate browser back button)
    const popstateEvent = new PopStateEvent('popstate', { state: null });
    popstateListeners.forEach(listener => listener(popstateEvent));

    // then
    expect(onBack).toHaveBeenCalledTimes(1);
  });

  it('removes event listener on unmount', () => {
    // given
    const onBack = vi.fn();
    const { unmount } = renderHook(() => useBrowserBackHandler(onBack));
    expect(popstateListeners.length).toBe(1);

    // when
    unmount();

    // then
    expect(window.removeEventListener).toHaveBeenCalledWith('popstate', expect.any(Function));
    expect(popstateListeners.length).toBe(0);
  });

  it('updates listener when onBack callback changes', () => {
    // given
    const onBack1 = vi.fn();
    const onBack2 = vi.fn();
    const { rerender } = renderHook(
      ({ callback }) => useBrowserBackHandler(callback),
      { initialProps: { callback: onBack1 } }
    );

    // when (trigger with first callback)
    const popstateEvent1 = new PopStateEvent('popstate', { state: null });
    popstateListeners.forEach(listener => listener(popstateEvent1));
    expect(onBack1).toHaveBeenCalledTimes(1);
    expect(onBack2).toHaveBeenCalledTimes(0);

    // when (change callback)
    rerender({ callback: onBack2 });

    // when (trigger with second callback)
    const popstateEvent2 = new PopStateEvent('popstate', { state: null });
    popstateListeners.forEach(listener => listener(popstateEvent2));

    // then
    expect(onBack1).toHaveBeenCalledTimes(1);
    expect(onBack2).toHaveBeenCalledTimes(1);
  });

  it('handles multiple instances independently', () => {
    // given
    const onBack1 = vi.fn();
    const onBack2 = vi.fn();

    // when
    renderHook(() => useBrowserBackHandler(onBack1));
    renderHook(() => useBrowserBackHandler(onBack2));

    // when (trigger browser back)
    const popstateEvent = new PopStateEvent('popstate', { state: null });
    popstateListeners.forEach(listener => listener(popstateEvent));

    // then (both callbacks should be called)
    expect(onBack1).toHaveBeenCalledTimes(1);
    expect(onBack2).toHaveBeenCalledTimes(1);
  });
});

