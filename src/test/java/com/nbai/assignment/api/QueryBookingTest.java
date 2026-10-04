package com.nbai.assignment.api;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.nbai.assignment.api.model.Booking;
import com.nbai.assignment.api.model.BookingDates;
import com.nbai.assignment.config.ConfigManager;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.testng.annotations.Test;

/**
 * Calls the real Restful Booker service (GET /booking with filters).
 * The sandbox is shared, so every test creates a booking with a unique firstname/lastname and scopes its query
 * to that name; results then cannot be affected by anyone else's data.
 */
public class QueryBookingTest {

    // The booking created by each test: 2026-07-01 to 2026-07-05.
    private static final String CHECKIN = "2026-07-01";
    private static final String CHECKOUT = "2026-07-05";

    private final BookingClient client = new BookingClient();

    @Test
    public void shouldFindBookingByFirstname() {
        String firstname = unique("First");
        String token = authToken();
        int bookingId = createBooking(newBooking(firstname, unique("Last")));
        try {
            Response response = client.getBookingsByQuery(Map.of("firstname", firstname));

            assertEquals(response.getStatusCode(), 200, "GET /booking?firstname status code");
            assertEquals(bookingIds(response), List.of(bookingId), "bookings matching the unique firstname");
        } finally {
            deleteQuietly(bookingId, token);
        }
    }

    @Test
    public void shouldFindBookingByLastname() {
        String lastname = unique("Last");
        String token = authToken();
        int bookingId = createBooking(newBooking(unique("First"), lastname));
        try {
            Response response = client.getBookingsByQuery(Map.of("lastname", lastname));

            assertEquals(response.getStatusCode(), 200, "GET /booking?lastname status code");
            assertEquals(bookingIds(response), List.of(bookingId), "bookings matching the unique lastname");
        } finally {
            deleteQuietly(bookingId, token);
        }
    }

    @Test
    public void shouldFindBookingByFirstnameAndLastname() {
        String firstname = unique("First");
        String lastname = unique("Last");
        String token = authToken();
        int bookingId = createBooking(newBooking(firstname, lastname));
        try {
            Response response = client.getBookingsByQuery(Map.of("firstname", firstname, "lastname", lastname));

            assertEquals(response.getStatusCode(), 200, "GET /booking?firstname&lastname status code");
            assertEquals(bookingIds(response), List.of(bookingId), "bookings matching both names");
        } finally {
            deleteQuietly(bookingId, token);
        }
    }

    @Test
    public void shouldReturnNoBookingsWhenNamesDoNotMatch() {
        String firstname = unique("First");
        String token = authToken();
        int bookingId = createBooking(newBooking(firstname, unique("Last")));
        try {
            // Right firstname, wrong lastname: both filters must match.
            Response mismatch = client.getBookingsByQuery(
                    Map.of("firstname", firstname, "lastname", unique("Other")));
            assertEquals(mismatch.getStatusCode(), 200, "GET /booking status code for mismatching names");
            assertTrue(bookingIds(mismatch).isEmpty(), "no booking should match a different lastname");

            // A name nobody has used.
            Response unknown = client.getBookingsByQuery(Map.of("firstname", unique("Nobody")));
            assertEquals(unknown.getStatusCode(), 200, "GET /booking status code for unknown firstname");
            assertTrue(bookingIds(unknown).isEmpty(), "no booking should match an unknown firstname");
        } finally {
            deleteQuietly(bookingId, token);
        }
    }

    /** Restful Booker's checkin filter returns bookings whose checkin date is after the given date. */
    @Test
    public void shouldFilterByCheckinDate() {
        String lastname = unique("Last");
        String token = authToken();
        int bookingId = createBooking(newBooking(unique("First"), lastname));
        try {
            Response earlier = client.getBookingsByQuery(Map.of("lastname", lastname, "checkin", "2026-06-01"));
            assertEquals(earlier.getStatusCode(), 200, "GET /booking?checkin (earlier date) status code");
            assertEquals(bookingIds(earlier), List.of(bookingId), "booking checking in after 2026-06-01");

            Response later = client.getBookingsByQuery(Map.of("lastname", lastname, "checkin", "2026-12-31"));
            assertEquals(later.getStatusCode(), 200, "GET /booking?checkin (later date) status code");
            assertTrue(bookingIds(later).isEmpty(), "booking does not check in after 2026-12-31");
        } finally {
            deleteQuietly(bookingId, token);
        }
    }

    /** Restful Booker's checkout filter returns bookings whose checkout date is before the given date. */
    @Test
    public void shouldFilterByCheckoutDate() {
        String lastname = unique("Last");
        String token = authToken();
        int bookingId = createBooking(newBooking(unique("First"), lastname));
        try {
            Response later = client.getBookingsByQuery(Map.of("lastname", lastname, "checkout", "2026-12-31"));
            assertEquals(later.getStatusCode(), 200, "GET /booking?checkout (later date) status code");
            assertEquals(bookingIds(later), List.of(bookingId), "booking checking out before 2026-12-31");

            Response earlier = client.getBookingsByQuery(Map.of("lastname", lastname, "checkout", "2026-06-01"));
            assertEquals(earlier.getStatusCode(), 200, "GET /booking?checkout (earlier date) status code");
            assertTrue(bookingIds(earlier).isEmpty(), "booking does not check out before 2026-06-01");
        } finally {
            deleteQuietly(bookingId, token);
        }
    }

    private static String unique(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private static Booking newBooking(String firstname, String lastname) {
        return new Booking(firstname, lastname, 150, true, new BookingDates(CHECKIN, CHECKOUT), "Parking");
    }

    private static List<Integer> bookingIds(Response response) {
        return response.jsonPath().getList("bookingid", Integer.class);
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
