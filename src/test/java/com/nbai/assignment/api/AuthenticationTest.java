package com.nbai.assignment.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import com.nbai.assignment.config.ConfigManager;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/**
 * Calls the real Restful Booker service.
 * Credentials come from RESTFUL_BOOKER_USERNAME / RESTFUL_BOOKER_PASSWORD; the test fails clearly if they are not set.
 */
public class AuthenticationTest {

    private final BookingClient client = new BookingClient();

    @Test
    public void shouldCreateAuthTokenWithValidCredentials() {
        Response response = client.createAuthToken(ConfigManager.getUsername(), ConfigManager.getPassword());

        assertEquals(response.getStatusCode(), 200, "POST /auth status code");

        // Restful Booker answers 200 even for bad credentials (body: {"reason":"Bad credentials"}),
        // so the token itself must be checked. Its value is deliberately never printed.
        String token = response.jsonPath().getString("token");
        String reason = response.jsonPath().getString("reason");
        assertNotNull(token, "auth response should contain a token (reason: " + reason + ")");
        assertFalse(token.isBlank(), "token should not be blank");
    }
}
