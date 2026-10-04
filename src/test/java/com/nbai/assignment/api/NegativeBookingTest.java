package com.nbai.assignment.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import com.nbai.assignment.api.model.Booking;
import com.nbai.assignment.api.model.BookingDates;
import com.nbai.assignment.config.ConfigManager;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/**
 * Negative and adversarial cases against the real Restful Booker service.
 * Each test creates the data it needs and checks both the rejection and that stored data was not changed.
 */
@Epic("Restful Booker API")
@Feature("Negative scenarios")
public class NegativeBookingTest {

    /** Deliberately wrong values, not real credentials. */
    private static final String WRONG_USERNAME = "no-such-user";
    private static final String WRONG_PASSWORD = "wrong-password";
    private static final String INVALID_TOKEN = "invalid-token";

    private final BookingClient client = new BookingClient();

    @Test
    public void shouldReturnNotFoundForNonExistentBooking() {
        Response response = client.getBooking(Integer.MAX_VALUE);

        assertEquals(response.getStatusCode(), 404, "GET /booking/{id} for an id that does not exist");
    }

    @Test
    public void shouldNotIssueTokenForInvalidCredentials() {
        Response response = client.createAuthToken(WRONG_USERNAME, WRONG_PASSWORD);

        // Restful Booker answers 200 even when credentials are wrong; the body tells the real outcome.
        assertEquals(response.getStatusCode(), 200, "POST /auth status code for invalid credentials");
        assertNull(response.jsonPath().getString("token"), "no token should be issued");
        assertEquals(response.jsonPath().getString("reason"), "Bad credentials", "failure reason");
    }

    @Test
    public void shouldRejectUpdateWithInvalidTokenAndLeaveBookingUnchanged() {
        Booking original = newBooking();
        Booking attemptedChange = new Booking("Hacker", "Attacker", 1, false,
                new BookingDates("2030-01-01", "2030-01-02"), "None");
        String token = authToken();
        int bookingId = createBooking(original);
        try {
            Response putResponse = client.updateBooking(bookingId, attemptedChange, INVALID_TOKEN);
            assertEquals(putResponse.getStatusCode(), 403, "PUT /booking/{id} with an invalid token");

            assertStillEquals(bookingId, original);
        } finally {
            deleteQuietly(bookingId, token);
        }
    }

    @Test
    public void shouldRejectDeleteWithInvalidTokenAndKeepBooking() {
        Booking original = newBooking();
        String token = authToken();
        int bookingId = createBooking(original);
        try {
            Response deleteResponse = client.deleteBooking(bookingId, INVALID_TOKEN);
            assertEquals(deleteResponse.getStatusCode(), 403, "DELETE /booking/{id} with an invalid token");

            assertStillEquals(bookingId, original);
        } finally {
            deleteQuietly(bookingId, token);
        }
    }

    @Test
    public void shouldRejectUpdateWithMissingRequiredFields() {
        Booking original = newBooking();
        // Valid token, but the payload has no names and no dates.
        Booking incomplete = new Booking(null, null, 0, false, null, null);
        String token = authToken();
        int bookingId = createBooking(original);
        try {
            Response putResponse = client.updateBooking(bookingId, incomplete, token);
            assertEquals(putResponse.getStatusCode(), 400, "PUT /booking/{id} with missing required fields");

            assertStillEquals(bookingId, original);
        } finally {
            deleteQuietly(bookingId, token);
        }
    }

    @Test
    public void shouldNotAllowDeletingNonExistentBooking() {
        String token = authToken();

        Response response = client.deleteBooking(Integer.MAX_VALUE, token);

        // Restful Booker answers 405 (not 404) when DELETE targets an id that does not exist.
        assertEquals(response.getStatusCode(), 405, "DELETE /booking/{id} for an id that does not exist");
    }

    private void assertStillEquals(int bookingId, Booking expected) {
        Response getResponse = client.getBooking(bookingId);
        assertEquals(getResponse.getStatusCode(), 200, "GET /booking/{id} after the rejected request");
        assertEquals(getResponse.as(Booking.class), expected, "booking must be unchanged");
    }

    private static Booking newBooking() {
        return new Booking("Nora", "Quinn", 320, true,
                new BookingDates("2026-08-01", "2026-08-04"), "Sea view");
    }

    private String authToken() {
        String token = client.createAuthToken(ConfigManager.getUsername(), ConfigManager.getPassword())
                .jsonPath().getString("token");
        assertNotNull(token, "an auth token is required (check RESTFUL_BOOKER_USERNAME / RESTFUL_BOOKER_PASSWORD)");
        return token;
    }

    private int createBooking(Booking booking) {
        Response response = client.createBooking(booking);
        assertEquals(response.getStatusCode(), 200, "precondition: POST /booking status code");
        return response.jsonPath().getInt("bookingid");
    }

    /** Best-effort cleanup: a failure is reported on stderr but never hides the test's own result. */
    private void deleteQuietly(int bookingId, String token) {
        try {
            int status = client.deleteBooking(bookingId, token).getStatusCode();
            if (status != 201) {
                System.err.println("CLEANUP WARNING: DELETE booking " + bookingId + " returned status " + status);
            }
        } catch (RuntimeException e) {
            System.err.println("CLEANUP WARNING: could not delete booking " + bookingId + ": " + e.getMessage());
        }
    }
}
