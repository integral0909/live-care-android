package app.client.munchbear.munchbearclient.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.client.munchbear.munchbearclient.model.PaginData;
import app.client.munchbear.munchbearclient.model.order.created.CreatedOrder;
import app.client.munchbear.munchbearclient.model.reservation.Links;
import app.client.munchbear.munchbearclient.model.reservation.Meta;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;

public class MyOrdersResponse extends PaginData {

    @SerializedName("data")
    private List<CreatedOrder> createdOrder;

    public MyOrdersResponse(Links links, Meta meta, List<CreatedOrder> createdOrder) {
        super(links, meta);
        this.createdOrder = createdOrder;
    }

    public List<CreatedOrder> getCreatedOrder() {
        return createdOrder;
    }

    public void setCreatedOrder(List<CreatedOrder> createdOrder) {
        this.createdOrder = createdOrder;
    }
}
