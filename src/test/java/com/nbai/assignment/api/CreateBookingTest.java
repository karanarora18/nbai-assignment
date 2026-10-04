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
@Feature("Create booking")
public class CreateBookingTest {

    private final BookingClient client = new BookingClient();

    @Test
    public void shouldCreateBookingWithValidPayload() {
        Booking submitted = newBooking();

        Response response = client.createBooking(submitted);
        JsonPath body = response.jsonPath();
        Integer bookingId = body.get("bookingid");
        try {
            assertEquals(response.getStatusCode(), 200, "POST /booking status code");

            assertNotNull(bookingId, "response should contain bookingid");
            assertTrue(bookingId > 0, "bookingid should be positive");
            assertNotNull(body.get("booking"), "response should contain a booking object");

            // Individual fields, compared with what was submitted.
            assertEquals(body.getString("booking.firstname"), submitted.getFirstname(), "firstname");
            assertEquals(body.getString("booking.lastname"), submitted.getLastname(), "lastname");
            assertEquals(body.getInt("booking.totalprice"), submitted.getTotalprice(), "totalprice");
            assertEquals(body.getBoolean("booking.depositpaid"), submitted.isDepositpaid(), "depositpaid");
            assertEquals(body.getString("booking.bookingdates.checkin"),
                    submitted.getBookingdates().getCheckin(), "checkin");
            assertEquals(body.getString("booking.bookingdates.checkout"),
                    submitted.getBookingdates().getCheckout(), "checkout");
            assertEquals(body.getString("booking.additionalneeds"), submitted.getAdditionalneeds(),
                    "additionalneeds");

            // Whole payload.
            assertEquals(body.getObject("booking", Booking.class), submitted, "returned booking");
        } finally {
            deleteQuietly(bookingId);
        }
    }

    private static Booking newBooking() {
        return new Booking("Jim", "Brown", 111, true,
                new BookingDates("2026-01-10", "2026-01-15"), "Breakfast");
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
