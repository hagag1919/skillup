package com.example.skillup.utils;

import java.util.regex.Pattern;

public class InputValidation {
    
    // SQL Injection patterns to detect
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        Pattern.compile("('|(\\-\\-)|(;)|(\\|)|(\\*)|(%))", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(insert|delete|update|create|drop|alter|exec|execute|script|select|union|into|from|where|join)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(or\\s+1\\s*=\\s*1|and\\s+1\\s*=\\s*1)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\\bor\\b.*\\bnull\\b|\\band\\b.*\\bnull\\b)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\\bor\\b.*\\b(true|false)\\b|\\band\\b.*\\b(true|false)\\b)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(sp_|xp_|cmdshell)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(\\<\\s*script|javascript:|vbscript:|onload|onerror|onclick)", Pattern.CASE_INSENSITIVE)
    };
    
    // XSS patterns
    private static final Pattern[] XSS_PATTERNS = {
        Pattern.compile("<\\s*script", Pattern.CASE_INSENSITIVE),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<\\s*iframe", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<\\s*object", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<\\s*embed", Pattern.CASE_INSENSITIVE),
        Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE)
    };
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Name validation pattern (letters, spaces, hyphens, apostrophes)
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s\\-']{2,100}$"
    );
    
    // Password strength pattern (at least 6 chars, containing letters and numbers)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$"
    );
    
    /**
     * Check if input contains SQL injection patterns
     */
    public static boolean containsSQLInjection(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String normalizedInput = input.toLowerCase().trim();
        
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(normalizedInput).find()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if input contains XSS patterns
     */
    public static boolean containsXSS(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String normalizedInput = input.toLowerCase().trim();
        
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(normalizedInput).find()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Sanitize string input by removing potentially harmful characters
     */
    public static String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove HTML/script tags
        String sanitized = input.replaceAll("<[^>]*>", "");
        
        // Remove dangerous script-related characters but keep normal punctuation
        sanitized = sanitized.replaceAll("[<>]", "");
        
        // Remove SQL comment patterns
        sanitized = sanitized.replaceAll("--", "");
        sanitized = sanitized.replaceAll("/\\*", "");
        sanitized = sanitized.replaceAll("\\*/", "");
        
        return sanitized.trim();
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email.trim()).matches() && 
               !containsObviousMaliciousContent(email);
    }
    
    /**
     * Validate name format
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        return NAME_PATTERN.matcher(name.trim()).matches() && 
               !containsObviousMaliciousContent(name);
    }
    
    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        return PASSWORD_PATTERN.matcher(password).matches() && 
               !containsObviousMaliciousContent(password);
    }
    
    /**
     * Validate role
     */
    public static boolean isValidRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }
        
        String normalizedRole = role.trim().toUpperCase();
        return ("STUDENT".equals(normalizedRole) || "INSTRUCTOR".equals(normalizedRole)) &&
               !containsObviousMaliciousContent(role);
    }
    
    /**
     * Validate bio text
     */
    public static boolean isValidBio(String bio) {
        if (bio == null) {
            return true; // Bio is optional
        }
        
        if (bio.length() > 1000) {
            return false; // Bio too long
        }
        
        // More lenient check for bio - only block obvious malicious patterns
        return !containsObviousMaliciousContent(bio);
    }
    
    /**
     * Check for obvious malicious content while allowing normal text
     */
    private static boolean containsObviousMaliciousContent(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String normalizedInput = input.toLowerCase().trim();
        
        // Check for script tags and obvious XSS
        if (normalizedInput.contains("<script") || 
            normalizedInput.contains("javascript:") || 
            normalizedInput.contains("vbscript:") ||
            normalizedInput.contains("<iframe") ||
            normalizedInput.contains("<object") ||
            normalizedInput.contains("<embed")) {
            return true;
        }
        
        // Check for obvious SQL injection patterns
        if (normalizedInput.contains("union select") ||
            normalizedInput.contains("drop table") ||
            normalizedInput.contains("delete from") ||
            normalizedInput.contains("insert into") ||
            normalizedInput.contains("update set") ||
            normalizedInput.matches(".*\\bor\\s+1\\s*=\\s*1\\b.*") ||
            normalizedInput.matches(".*\\band\\s+1\\s*=\\s*1\\b.*")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Validate search query
     */
    public static boolean isValidSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        
        if (query.length() > 100) {
            return false; // Query too long
        }
        
        return !containsObviousMaliciousContent(query);
    }
    
    /**
     * Validate ID parameter
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }
    
    /**
     * Comprehensive input validation for user registration
     */
    public static ValidationResult validateUserRegistration(String name, String email, String password, String role, String bio) {
        ValidationResult result = new ValidationResult();
        
        // Validate name
        if (!isValidName(name)) {
            result.addError("Invalid name format. Name must contain only letters, spaces, hyphens, and apostrophes (2-100 characters)");
        }
        
        // Validate email
        if (!isValidEmail(email)) {
            result.addError("Invalid email format");
        }
        
        // Validate password
        if (!isValidPassword(password)) {
            result.addError("Password must be at least 6 characters long and contain both letters and numbers");
        }
        
        // Validate role
        if (!isValidRole(role)) {
            result.addError("Role must be either STUDENT or INSTRUCTOR");
        }
        
        // Validate bio
        if (!isValidBio(bio)) {
            result.addError("Bio contains invalid characters or is too long (max 1000 characters)");
        }
        
        return result;
    }
    
    /**
     * Comprehensive input validation for user login
     */
    public static ValidationResult validateUserLogin(String email, String password) {
        ValidationResult result = new ValidationResult();
        
        // Validate email
        if (!isValidEmail(email)) {
            result.addError("Invalid email format");
        }
        
        // Basic password validation (not strength, just safety)
        if (password == null || password.trim().isEmpty()) {
            result.addError("Password is required");
        } else if (containsObviousMaliciousContent(password)) {
            result.addError("Password contains invalid characters");
        }
        
        return result;
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();
        
        public void addError(String error) {
            this.valid = false;
            if (errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(error);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrors() {
            return errors.toString();
        }
    }
}
