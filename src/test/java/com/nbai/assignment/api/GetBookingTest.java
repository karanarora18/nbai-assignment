package com.nbai.assignment.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.nbai.assignment.api.model.Booking;
import com.nbai.assignment.api.model.BookingDates;
import com.nbai.assignment.config.ConfigManager;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;

/** Calls the real Restful Booker service. Each test creates the data it needs. */
@Epic("Restful Booker API")
@Feature("Get booking")
public class GetBookingTest {

    private final BookingClient client = new BookingClient();

    @Test
    public void shouldRetrieveCreatedBookingWithMatchingData() {
        // 1. Create a booking.
        Booking submitted = newBooking();
        Response createResponse = client.createBooking(submitted);
        Integer bookingId = createResponse.jsonPath().get("bookingid");
        try {
            // Precondition: the booking must exist before it can be retrieved.
            assertEquals(createResponse.getStatusCode(), 200, "POST /booking status code");
            assertNotNull(bookingId, "create response should contain bookingid");
            assertTrue(bookingId > 0, "bookingid should be positive");

            // 2. Retrieve it by the id the API returned.
            Response getResponse = client.getBooking(bookingId);
            JsonPath body = getResponse.jsonPath();

            assertEquals(getResponse.getStatusCode(), 200, "GET /booking/{id} status code");

            // 3. Compare field by field, then as a whole object.
            assertEquals(body.getString("firstname"), submitted.getFirstname(), "firstname");
            assertEquals(body.getString("lastname"), submitted.getLastname(), "lastname");
            assertEquals(body.getInt("totalprice"), submitted.getTotalprice(), "totalprice");
            assertEquals(body.getBoolean("depositpaid"), submitted.isDepositpaid(), "depositpaid");
            assertEquals(body.getString("bookingdates.checkin"),
                    submitted.getBookingdates().getCheckin(), "checkin");
            assertEquals(body.getString("bookingdates.checkout"),
                    submitted.getBookingdates().getCheckout(), "checkout");
            assertEquals(body.getString("additionalneeds"), submitted.getAdditionalneeds(), "additionalneeds");

            assertEquals(getResponse.as(Booking.class), submitted, "retrieved booking");
        } finally {
            deleteQuietly(bookingId);
        }
    }

    private static Booking newBooking() {
        return new Booking("Sally", "Green", 250, false,
                new BookingDates("2026-02-01", "2026-02-05"), "Late checkout");
    }

    /** Best-effort cleanup: a failure here is reported but never hides the test's own result. */
    private void deleteQuietly(Integer bookingId) {
        if (bookingId == null) {
            return;
        }
        try {
            String token = client.createAuthToken(ConfigManager.getUsername(), ConfigManager.getPassword())
                    .jsonPath().getString("token");
            int status = client.deleteBooking(bookingId, token).getStatusCode();
            if (status != 201) {
                System.err.println("Cleanup: DELETE booking " + bookingId + " returned status " + status);
            }
        } catch (RuntimeException e) {
            System.err.println("Cleanup: could not delete booking " + bookingId + ": " + e.getMessage());
        }
    }
}
