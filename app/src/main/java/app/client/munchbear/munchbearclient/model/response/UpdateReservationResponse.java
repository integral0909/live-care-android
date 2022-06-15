package app.client.munchbear.munchbearclient.model.response;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.reservation.Reservation;

/**
 * @author Roman H.
 */

public class UpdateReservationResponse {

    @SerializedName("data")
    private Reservation reservation;

    public UpdateReservationResponse(Reservation reservation) {
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
