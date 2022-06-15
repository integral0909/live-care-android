package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import app.client.munchbear.munchbearclient.model.order.Cart;

/*
 * @author Nazar V.
 */
public class PreMenuActivityViewModel extends ViewModel {

    private final MutableLiveData<Integer> navigateMenuFragment = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public MutableLiveData<Integer> getNavigateMenuFragment() {
        return navigateMenuFragment;
    }

    public void handleNavigateMenuFragmentClick(int openAddressType, int deliveryType) {
        Cart.getInstance().setSelectedDeliveryType(deliveryType);
        navigateMenuFragment.setValue(openAddressType);
    }
}
