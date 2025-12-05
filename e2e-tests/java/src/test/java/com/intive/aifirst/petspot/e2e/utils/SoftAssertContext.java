package com.intive.aifirst.petspot.e2e.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Thread-safe context for tracking soft assertion failures.
 * Soft assertions log failures but don't stop the test execution.
 * At the end of the scenario, all failures are reported.
 */
public class SoftAssertContext {
    
    private static final ThreadLocal<List<SoftFailure>> failures = ThreadLocal.withInitial(ArrayList::new);
    
    /**
     * Records a soft assertion failure with step name.
     * 
     * @param stepName Name of the step where failure occurred
     * @param message Description of the failure
     */
    public static void addFailure(String stepName, String message) {
        failures.get().add(new SoftFailure(stepName, message));
        System.out.println("⚠️ SOFT ASSERT FAILED [" + stepName + "]: " + message);
    }
    
    /**
     * Records a soft assertion success.
     * 
     * @param message Description of what passed
     */
    public static void addSuccess(String message) {
        System.out.println("✅ SOFT ASSERT PASSED: " + message);
    }
    
    /**
     * Represents a single soft assertion failure.
     */
    public static class SoftFailure {
        public final String stepName;
        public final String message;
        
        public SoftFailure(String stepName, String message) {
            this.stepName = stepName;
            this.message = message;
        }
        
        @Override
        public String toString() {
            return "[" + stepName + "] " + message;
        }
    }
    
    /**
     * Checks if there are any soft assertion failures.
     * 
     * @return true if there are failures
     */
    public static boolean hasFailures() {
        return !failures.get().isEmpty();
    }
    
    /**
     * Gets all recorded failures.
     * 
     * @return List of soft failures
     */
    public static List<SoftFailure> getFailures() {
        return new ArrayList<>(failures.get());
    }
    
    /**
     * Clears all recorded failures.
     * Should be called at the start of each scenario.
     */
    public static void clear() {
        failures.get().clear();
    }
    
    /**
     * Gets a summary of all failures for reporting.
     * 
     * @return Formatted string with all failures
     */
    public static String getFailureSummary() {
        List<SoftFailure> failureList = failures.get();
        if (failureList.isEmpty()) {
            return "No soft assertion failures";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("SOFT ASSERTION FAILURES (").append(failureList.size()).append("):\n");
        for (int i = 0; i < failureList.size(); i++) {
            SoftFailure f = failureList.get(i);
            sb.append("  ").append(i + 1).append(". STEP: \"").append(f.stepName).append("\"\n");
            sb.append("     ").append(f.message).append("\n");
        }
        return sb.toString();
    }
}

