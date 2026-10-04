package com.nbai.assignment.config;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/** Pure logic tests: no environment changes and no network calls. */
public class ConfigManagerTest {

    @Test
    public void usesDefaultWhenOverrideIsNull() {
        assertEquals(ConfigManager.resolveBaseUri(null), ConfigManager.DEFAULT_BASE_URI);
    }

    @Test
    public void usesDefaultWhenOverrideIsBlank() {
        assertEquals(ConfigManager.resolveBaseUri("   "), ConfigManager.DEFAULT_BASE_URI);
    }

    @Test
    public void usesOverrideWhenProvided() {
        assertEquals(ConfigManager.resolveBaseUri("http://localhost:3001"), "http://localhost:3001");
    }

    @Test
    public void trimsWhitespaceAroundOverride() {
        assertEquals(ConfigManager.resolveBaseUri("  http://localhost:3001  "), "http://localhost:3001");
    }
}
