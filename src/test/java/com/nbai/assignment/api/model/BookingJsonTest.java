package com.nbai.assignment.api.model;

import static org.testng.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

/** Checks the models against Jackson locally. No network calls. */
public class BookingJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private static Booking sampleBooking() {
        return new Booking("Jim", "Brown", 111, true,
                new BookingDates("2018-01-01", "2019-01-01"), "Breakfast");
    }

    @Test
    public void serializesToRestfulBookerFieldNames() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(sampleBooking()));

        assertEquals(json.get("firstname").asText(), "Jim");
        assertEquals(json.get("lastname").asText(), "Brown");
        assertEquals(json.get("totalprice").asInt(), 111);
        assertEquals(json.get("depositpaid").asBoolean(), true);
        assertEquals(json.get("bookingdates").get("checkin").asText(), "2018-01-01");
        assertEquals(json.get("bookingdates").get("checkout").asText(), "2019-01-01");
        assertEquals(json.get("additionalneeds").asText(), "Breakfast");
        assertEquals(json.size(), 6);
    }

    @Test
    public void deserializesRestfulBookerResponseBody() throws Exception {
        String body = "{\"firstname\":\"Jim\",\"lastname\":\"Brown\",\"totalprice\":111,\"depositpaid\":true,"
                + "\"bookingdates\":{\"checkin\":\"2018-01-01\",\"checkout\":\"2019-01-01\"},"
                + "\"additionalneeds\":\"Breakfast\"}";

        assertEquals(mapper.readValue(body, Booking.class), sampleBooking());
    }

    @Test
    public void roundTripPreservesAllFields() throws Exception {
        Booking original = sampleBooking();

        Booking copy = mapper.readValue(mapper.writeValueAsString(original), Booking.class);

        assertEquals(copy, original);
    }
}
