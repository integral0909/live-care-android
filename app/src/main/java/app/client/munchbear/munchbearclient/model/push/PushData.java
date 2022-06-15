package app.client.munchbear.munchbearclient.model.push;

import com.google.gson.annotations.SerializedName;

/**
 * @author Roman H.
 */


public class PushData {

    @SerializedName("data")
    PushOrder pushOrder;

    public PushData(PushOrder pushOrder) {
        this.pushOrder = pushOrder;
    }

    public PushOrder getPushOrder() {
        return pushOrder;
    }

    public void setPushOrder(PushOrder pushOrder) {
        this.pushOrder = pushOrder;
    }
}
