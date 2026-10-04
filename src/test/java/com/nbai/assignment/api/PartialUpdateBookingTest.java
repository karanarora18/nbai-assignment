package com.nbai.assignment.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.nbai.assignment.api.model.Booking;
import com.nbai.assignment.api.model.BookingDates;
import com.nbai.assignment.config.ConfigManager;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.Map;
import org.testng.annotations.Test;

/** Calls the real Restful Booker service (PATCH /booking/{id}). Each test creates the data it needs. */
public class PartialUpdateBookingTest {

    private final BookingClient client = new BookingClient();

    @Test
    public void shouldChangeOnlyPatchedFieldsUsingPatch() {
        Booking original = new Booking("Sally", "Green", 250, false,
                new BookingDates("2026-03-01", "2026-03-05"), "Late checkout");
        Map<String, Object> patch = Map.of("firstname", "Samantha", "totalprice", 300);
        // What the booking should look like afterwards: patched fields changed, everything else as created.
        Booking expected = new Booking("Samantha", original.getLastname(), 300, original.isDepositpaid(),
                original.getBookingdates(), original.getAdditionalneeds());

        String token = client.createAuthToken(ConfigManager.getUsername(), ConfigManager.getPassword())
                .jsonPath().getString("token");
        assertNotNull(token, "an auth token is required (check RESTFUL_BOOKER_USERNAME / RESTFUL_BOOKER_PASSWORD)");

        Response createResponse = client.createBooking(original);
        assertEquals(createResponse.getStatusCode(), 200, "precondition: POST /booking status code");
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        try {
            Response patchResponse = client.partialUpdateBooking(bookingId, patch, token);
            assertEquals(patchResponse.getStatusCode(), 200, "PATCH /booking/{id} status code");

            Response getResponse = client.getBooking(bookingId);
            assertEquals(getResponse.getStatusCode(), 200, "GET /booking/{id} status code after PATCH");
            JsonPath body = getResponse.jsonPath();

            // Patched fields changed.
            assertEquals(body.getString("firstname"), "Samantha", "patched firstname");
            assertEquals(body.getInt("totalprice"), 300, "patched totalprice");

            // Fields that were not patched stayed as created.
            assertEquals(body.getString("lastname"), original.getLastname(), "lastname should be unchanged");
            assertEquals(body.getBoolean("depositpaid"), original.isDepositpaid(), "depositpaid should be unchanged");
            assertEquals(body.getString("bookingdates.checkin"),
                    original.getBookingdates().getCheckin(), "checkin should be unchanged");
            assertEquals(body.getString("bookingdates.checkout"),
                    original.getBookingdates().getCheckout(), "checkout should be unchanged");
            assertEquals(body.getString("additionalneeds"),
                    original.getAdditionalneeds(), "additionalneeds should be unchanged");

            assertEquals(getResponse.as(Booking.class), expected, "retrieved booking after PATCH");
        } finally {
            deleteQuietly(bookingId, token);
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
