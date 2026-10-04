package com.nbai.assignment.config;

/** Single place that decides which Restful Booker base URI the framework talks to. */
public final class ConfigManager {

    public static final String BASE_URI_ENV_VAR = "RESTFUL_BOOKER_BASE_URI";
    public static final String DEFAULT_BASE_URI = "https://restful-booker.herokuapp.com";
    public static final String USERNAME_ENV_VAR = "RESTFUL_BOOKER_USERNAME";
    public static final String PASSWORD_ENV_VAR = "RESTFUL_BOOKER_PASSWORD";

    private ConfigManager() {
    }

    /** Returns the base URI from RESTFUL_BOOKER_BASE_URI, or the default if it is unset or blank. */
    public static String getBaseUri() {
        return resolveBaseUri(System.getenv(BASE_URI_ENV_VAR));
    }

    /** Returns RESTFUL_BOOKER_USERNAME. Fails fast if it is not set, because there is no default credential. */
    public static String getUsername() {
        return requireValue(USERNAME_ENV_VAR, System.getenv(USERNAME_ENV_VAR));
    }

    /** Returns RESTFUL_BOOKER_PASSWORD. Fails fast if it is not set, because there is no default credential. */
    public static String getPassword() {
        return requireValue(PASSWORD_ENV_VAR, System.getenv(PASSWORD_ENV_VAR));
    }

    /** Picks the override when it has text, otherwise the default. Kept separate so it is testable without touching the real environment. */
    static String resolveBaseUri(String override) {
        if (override == null || override.isBlank()) {
            return DEFAULT_BASE_URI;
        }
        return override.trim();
    }

    /** Returns the value, or throws a clear error naming the missing variable. */
    static String requireValue(String name, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Environment variable " + name + " is not set");
        }
        return value;
    }
}
