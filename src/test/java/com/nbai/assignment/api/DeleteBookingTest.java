package com.nbai.assignment.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.nbai.assignment.api.model.Booking;
import com.nbai.assignment.api.model.BookingDates;
import com.nbai.assignment.config.ConfigManager;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/** Calls the real Restful Booker service (DELETE /booking/{id}). Each test creates the data it needs. */
public class DeleteBookingTest {

    private final BookingClient client = new BookingClient();

    @Test
    public void shouldDeleteBookingAndReturnNotFoundAfterwards() {
        Booking booking = new Booking("Mark", "Stone", 175, true,
                new BookingDates("2026-06-01", "2026-06-03"), "Extra bed");

        String token = client.createAuthToken(ConfigManager.getUsername(), ConfigManager.getPassword())
                .jsonPath().getString("token");
        assertNotNull(token, "an auth token is required (check RESTFUL_BOOKER_USERNAME / RESTFUL_BOOKER_PASSWORD)");

        Response createResponse = client.createBooking(booking);
        assertEquals(createResponse.getStatusCode(), 200, "precondition: POST /booking status code");
        int bookingId = createResponse.jsonPath().getInt("bookingid");

        boolean deleted = false;
        try {
            // Restful Booker answers a successful DELETE with 201 Created.
            Response deleteResponse = client.deleteBooking(bookingId, token);
            assertEquals(deleteResponse.getStatusCode(), 201, "DELETE /booking/{id} status code");
            deleted = true;

            Response getResponse = client.getBooking(bookingId);
            assertEquals(getResponse.getStatusCode(), 404, "GET /booking/{id} status code after DELETE");
        } finally {
            // Only clean up if the delete itself did not succeed; never delete twice.
            if (!deleted) {
                deleteQuietly(bookingId, token);
            }
        }
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
