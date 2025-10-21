package com.taskManager.tasks.util;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class SanitizerUtil {
    
    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.BLOCKS);
    
    public static String sanitize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        // Remove all HTML tags and scripts
        return POLICY.sanitize(input).trim();
    }
}
