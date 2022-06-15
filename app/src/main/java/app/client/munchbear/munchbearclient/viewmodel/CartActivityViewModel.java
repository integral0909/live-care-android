package app.client.munchbear.munchbearclient.viewmodel;

import android.app.AlertDialog;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import io.reactivex.disposables.CompositeDisposable;

public class CartActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<Dish> dishEditLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> totalSumValidLiveData = new MutableLiveData<>();

    public void editDish(Dish dish, int position) {
        List<Dish> dishList = Cart.getInstance().getDishList();
        dishList.set(position, dish);
        Cart.getInstance().setDishList(dishList);

        Cart.getInstance().updateCartPrices();
        dishEditLiveData.setValue(dish);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void checkCartTotalPrice(Context context) {
        if (Cart.getInstance().isTotalSumMore()) {
            totalSumValidLiveData.setValue(true);
        } else {
            showLowTotalSumDialog(context);
        }
    }

    private void showLowTotalSumDialog(Context context) {
        int minSum = (int) Cart.getInstance().getSelectedDeliveryZone().getMinOrderInDollar();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(String.format(context.getResources().getString(R.string.cart_not_valid_total_sum), String.valueOf(minSum)));
        builder.setNegativeButton("OK", (dialogInterface, i) -> dialogInterface.cancel());
        builder.create().show();
    }

    public MutableLiveData<Dish> getDishEditLiveData() {
        return dishEditLiveData;
    }
    public MutableLiveData<Boolean> getTotalSumValidLiveData() {
        return totalSumValidLiveData;
    }
}
