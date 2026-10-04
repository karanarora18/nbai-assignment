package com.nbai.assignment.api;

import static io.restassured.RestAssured.given;

import com.nbai.assignment.api.model.Booking;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

/**
 * Encapsulates every Restful Booker endpoint used by the tests.
 * It only sends requests and returns the Response; assertions belong in the tests.
 * Nothing is sent until a method is called.
 */
public class BookingClient {

    private static final String AUTH_PATH = "/auth";
    private static final String BOOKING_PATH = "/booking";
    private static final String BOOKING_BY_ID_PATH = "/booking/{id}";

    /** Restful Booker expects the auth token as a cookie named "token" on PUT/PATCH/DELETE. */
    private static final String TOKEN_COOKIE = "token";

    private final RequestSpecification spec;

    public BookingClient() {
        this(RequestSpecFactory.create());
    }

    public BookingClient(RequestSpecification spec) {
        this.spec = spec;
    }

    /** POST /auth. The token is in the response body field "token". */
    public Response createAuthToken(String username, String password) {
        return jsonRequest()
                .body(Map.of("username", username, "password", password))
                .post(AUTH_PATH);
    }

    /** POST /booking. Response body contains "bookingid" and the created "booking". */
    public Response createBooking(Booking booking) {
        return jsonRequest()
                .body(booking)
                .post(BOOKING_PATH);
    }

    /** GET /booking/{id}. */
    public Response getBooking(int bookingId) {
        return jsonRequest()
                .get(BOOKING_BY_ID_PATH, bookingId);
    }

    /** PUT /booking/{id}: full update. Requires an auth token. */
    public Response updateBooking(int bookingId, Booking booking, String token) {
        return jsonRequest()
                .cookie(TOKEN_COOKIE, token)
                .body(booking)
                .put(BOOKING_BY_ID_PATH, bookingId);
    }

    /** PATCH /booking/{id}: partial update. Send only the fields to change, e.g. Map.of("firstname", "Sam"). Requires an auth token. */
    public Response partialUpdateBooking(int bookingId, Map<String, Object> fields, String token) {
        return jsonRequest()
                .cookie(TOKEN_COOKIE, token)
                .body(fields)
                .patch(BOOKING_BY_ID_PATH, bookingId);
    }

    /** DELETE /booking/{id}. Requires an auth token. */
    public Response deleteBooking(int bookingId, String token) {
        return jsonRequest()
                .cookie(TOKEN_COOKIE, token)
                .delete(BOOKING_BY_ID_PATH, bookingId);
    }

    /**
     * GET /booking with optional filters. Supported keys: firstname, lastname, checkin, checkout.
     * Pass only the filters you need; an empty map lists all booking ids.
     */
    public Response getBookingsByQuery(Map<String, String> queryParams) {
        return jsonRequest()
                .queryParams(queryParams)
                .get(BOOKING_PATH);
    }

    private RequestSpecification jsonRequest() {
        return given()
                .spec(spec)
                .contentType(ContentType.JSON)
                // Plain string on purpose: accept(ContentType.JSON) makes REST Assured send a multi-value list,
                // which Restful Booker answers with HTTP 418.
                .header("Accept", "application/json");
    }
}
