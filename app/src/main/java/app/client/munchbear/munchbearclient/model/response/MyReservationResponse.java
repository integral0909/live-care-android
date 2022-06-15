package app.client.munchbear.munchbearclient.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.client.munchbear.munchbearclient.model.PaginData;
import app.client.munchbear.munchbearclient.model.reservation.CreatedReservation;
import app.client.munchbear.munchbearclient.model.reservation.Links;
import app.client.munchbear.munchbearclient.model.reservation.Meta;

/**
 * @author Roman H.
 */

public class MyReservationResponse extends PaginData {

    @SerializedName("data")
    private List<CreatedReservation> createdReservationList;

    public MyReservationResponse(Links links, Meta meta, List<CreatedReservation> createdReservationList) {
        super(links, meta);
        this.createdReservationList = createdReservationList;
    }

    public List<CreatedReservation> getCreatedReservationList() {
        return createdReservationList;
    }

    public void setCreatedReservationList(List<CreatedReservation> createdReservationList) {
        this.createdReservationList = createdReservationList;
    }

}
