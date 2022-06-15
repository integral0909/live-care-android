package app.client.munchbear.munchbearclient.model;

import com.google.gson.annotations.SerializedName;

import app.client.munchbear.munchbearclient.model.reservation.Links;
import app.client.munchbear.munchbearclient.model.reservation.Meta;

public class PaginData {

    @SerializedName("links")
    private Links links;

    @SerializedName("meta")
    private Meta meta;

    public PaginData(Links links, Meta meta) {
        this.links = links;
        this.meta = meta;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

}
