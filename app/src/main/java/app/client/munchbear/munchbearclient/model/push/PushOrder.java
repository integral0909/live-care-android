package app.client.munchbear.munchbearclient.model.push;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.reservation.Reservation;

/**
 * @author Roman H.
 */

public class PushOrder {

    @SerializedName("resource")
    private String resource;

    @SerializedName("reservation")
    private Reservation reservation;

    @SerializedName("event")
    private String event;

    public PushOrder(String resource, Reservation reservation, String event) {
        this.resource = resource;
        this.reservation = reservation;
        this.event = event;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
