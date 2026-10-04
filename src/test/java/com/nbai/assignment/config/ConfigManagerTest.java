package com.nbai.assignment.config;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

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

    @Test
    public void requireValueReturnsConfiguredValue() {
        assertEquals(ConfigManager.requireValue("SOME_VAR", "secret"), "secret");
    }

    @Test
    public void requireValueFailsWhenMissingOrBlank() {
        assertThrows(IllegalStateException.class, () -> ConfigManager.requireValue("SOME_VAR", null));
        assertThrows(IllegalStateException.class, () -> ConfigManager.requireValue("SOME_VAR", " "));
    }

    @Test
    public void requireValueErrorNamesTheVariable() {
        try {
            ConfigManager.requireValue("SOME_VAR", null);
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("SOME_VAR"));
            return;
        }
        throw new AssertionError("expected IllegalStateException");
    }
}
