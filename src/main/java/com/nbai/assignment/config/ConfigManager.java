package com.nbai.assignment.config;

/** Single place that decides which Restful Booker base URI the framework talks to. */
public final class ConfigManager {

    public static final String BASE_URI_ENV_VAR = "RESTFUL_BOOKER_BASE_URI";
    public static final String DEFAULT_BASE_URI = "https://restful-booker.herokuapp.com";

    private ConfigManager() {
    }

    /** Returns the base URI from RESTFUL_BOOKER_BASE_URI, or the default if it is unset or blank. */
    public static String getBaseUri() {
        return resolveBaseUri(System.getenv(BASE_URI_ENV_VAR));
    }

    /** Picks the override when it has text, otherwise the default. Kept separate so it is testable without touching the real environment. */
    static String resolveBaseUri(String override) {
        if (override == null || override.isBlank()) {
            return DEFAULT_BASE_URI;
        }
        return override.trim();
    }
}
