package com.nbai.assignment.api.model;

import java.util.Objects;

/** Check-in / check-out dates of a booking. Dates are kept as "yyyy-MM-dd" strings, exactly as the API sends them. */
public class BookingDates {

    private String checkin;
    private String checkout;

    /** Needed by Jackson for deserialization. */
    public BookingDates() {
    }

    public BookingDates(String checkin, String checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public String getCheckin() {
        return checkin;
    }

    public void setCheckin(String checkin) {
        this.checkin = checkin;
    }

    public String getCheckout() {
        return checkout;
    }

    public void setCheckout(String checkout) {
        this.checkout = checkout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookingDates)) {
            return false;
        }
        BookingDates that = (BookingDates) o;
        return Objects.equals(checkin, that.checkin) && Objects.equals(checkout, that.checkout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkin, checkout);
    }

    @Override
    public String toString() {
        return "BookingDates{checkin=" + checkin + ", checkout=" + checkout + "}";
    }
}
