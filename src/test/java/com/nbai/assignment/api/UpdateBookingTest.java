package com.nbai.assignment.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.nbai.assignment.api.model.Booking;
import com.nbai.assignment.api.model.BookingDates;
import com.nbai.assignment.config.ConfigManager;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/** Calls the real Restful Booker service (PUT /booking/{id}). Each test creates the data it needs. */
@Epic("Restful Booker API")
@Feature("Update booking")
public class UpdateBookingTest {

    private final BookingClient client = new BookingClient();

    @Test
    public void shouldReplaceWholeBookingUsingPut() {
        Booking original = new Booking("Jim", "Brown", 111, true,
                new BookingDates("2026-04-01", "2026-04-05"), "Breakfast");
        // Every field differs from the original so a partial update could not pass by accident.
        Booking updated = new Booking("Alice", "Walker", 480, false,
                new BookingDates("2026-05-10", "2026-05-20"), "Airport pickup");

        String token = client.createAuthToken(ConfigManager.getUsername(), ConfigManager.getPassword())
                .jsonPath().getString("token");
        assertNotNull(token, "an auth token is required (check RESTFUL_BOOKER_USERNAME / RESTFUL_BOOKER_PASSWORD)");

        Response createResponse = client.createBooking(original);
        assertEquals(createResponse.getStatusCode(), 200, "precondition: POST /booking status code");
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        try {
            Response putResponse = client.updateBooking(bookingId, updated, token);
            assertEquals(putResponse.getStatusCode(), 200, "PUT /booking/{id} status code");
            assertEquals(putResponse.as(Booking.class), updated, "booking returned by PUT");

            Response getResponse = client.getBooking(bookingId);
            assertEquals(getResponse.getStatusCode(), 200, "GET /booking/{id} status code after PUT");
            JsonPath body = getResponse.jsonPath();
            assertEquals(body.getString("firstname"), updated.getFirstname(), "firstname");
            assertEquals(body.getString("lastname"), updated.getLastname(), "lastname");
            assertEquals(body.getInt("totalprice"), updated.getTotalprice(), "totalprice");
            assertEquals(body.getBoolean("depositpaid"), updated.isDepositpaid(), "depositpaid");
            assertEquals(body.getString("bookingdates.checkin"),
                    updated.getBookingdates().getCheckin(), "checkin");
            assertEquals(body.getString("bookingdates.checkout"),
                    updated.getBookingdates().getCheckout(), "checkout");
            assertEquals(body.getString("additionalneeds"), updated.getAdditionalneeds(), "additionalneeds");
            assertEquals(getResponse.as(Booking.class), updated, "retrieved booking after PUT");
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
