package app.client.munchbear.munchbearclient.view.adapter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.CartChangeListener;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.utils.ImageUtils;
import app.client.munchbear.munchbearclient.utils.LoyaltyProgram;
import app.client.munchbear.munchbearclient.utils.TextUtils;
import app.client.munchbear.munchbearclient.view.CartActivity;
import app.client.munchbear.munchbearclient.viewmodel.CartActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Roman on 7/21/2018.
 */
public class CartDishAdapter extends RecyclerView.Adapter<CartDishAdapter.CartDishViewHolder> {

    private Context context;
    private boolean editable;
    private List<Dish> dishList = new ArrayList<>();
    private CartActivityViewModel cartActivityViewModel;
    private CartChangeListener cartChangeListener;

    public CartDishAdapter(Context cont, boolean editable, List<Dish> dishList, CartChangeListener listener) {
        this.context = cont;
        this.editable = editable;
        this.dishList = dishList;
        this.cartChangeListener = listener;
    }

    @Override
    public CartDishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_dish_item, parent, false);

        cartActivityViewModel = ViewModelProviders.of((CartActivity)context).get(CartActivityViewModel.class);

        return new CartDishViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartDishViewHolder holder, int position) {
        holder.bindData(dishList.get(position));
    }

    private void deleteListItem(int position) {
        dishList.remove(position);
        Cart.getInstance().updateCartPrices();

        notifyItemRemoved(position);
        checkLastDeletedItem();
    }

    private void checkLastDeletedItem() {
        if (dishList.size() == 0) {
            Cart.getInstance().setCartEmpty(true);
            Cart.getInstance().setTotalPricePoint(0);
            Cart.getInstance().setTotalPriceDollar(0);
            cartChangeListener.clearCart();
        } else {
            cartChangeListener.deleteDish();
        }
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public class CartDishViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.editBtn) ImageView editBtn;
        @BindView(R.id.deleteBtn) ImageView deleteBtn;
        @BindView(R.id.imageStar) ImageView imageStar;
        @BindView(R.id.dishImage) RoundedImageView dishImage;
        @BindView(R.id.dishName) TextView dishName;
        @BindView(R.id.dishSize) TextView dishSize;
        @BindView(R.id.mandatoryModifierTxt) TextView mandatoryModifierTxt;
        @BindView(R.id.counterModifierTxt) TextView counterModifierTxt;
        @BindView(R.id.excludeModifierTxt) TextView excludeModifierTxt;
        @BindView(R.id.totalSumDollar) TextView totalSumDollar;
        @BindView(R.id.totalSumPoint) TextView totalSumPoint;
        @BindView(R.id.orTxt) TextView orTxt;

        private Dish dish;
        private Resources resources;

        public CartDishViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(Dish d) {
            dish = d;
            resources = context.getResources();

            checkEditableViews();
            LoyaltyProgram.setupCartViews(orTxt, imageStar, totalSumPoint, totalSumDollar);

            initDishName();
            dishSize.setText(String.format(resources.getString(R.string.cart_dish_size_format),
                    dish.getSelectedDishSize().getSizeTitle()));

            initMandatoryModifier();

            initModifierField(excludeModifierTxt, true);
            initModifierField(counterModifierTxt, false);

            ImageUtils.setImageFromUrl(dish.getImgUrl(), dishImage, R.mipmap.dish_default_avatart);
            TextUtils.setupTotalPrice(context, totalSumDollar, totalSumPoint, dish.getCostDollar(),
                    dish.getCostPoint(), dish.getCount());

        }

        private void initModifierField(TextView textView, boolean isExclude) {
            if (dish.getSelectedModifierRecursion(isExclude).size() > 0) {
                textView.setText(dish.getModifiersTitle(context, isExclude));
            } else {
              textView.setVisibility(View.GONE);
            }
        }

        private void initMandatoryModifier() {
            if (dish.getMandatoryModifierTitles(context).length() > 0) {
                mandatoryModifierTxt.setText(dish.getMandatoryModifierTitles(context));
            } else {
                mandatoryModifierTxt.setVisibility(View.GONE);
            }
        }

        private void checkEditableViews() {
            editBtn.setVisibility(editable ? View.VISIBLE : View.GONE);
            deleteBtn.setVisibility(editable ? View.VISIBLE : View.GONE);
        }

        private void initDishName() {
            String name = dish.getName();
            String count = String.valueOf(dish.getCount());
            dishName.setText(dish.getCount() > 1 ? String.format(resources.getString(R.string.cart_dish_name_many_format),
                    name, count) : name);
        }

        @OnClick(R.id.deleteBtn)
        public void clickDelete() {
            deleteListItem(getAdapterPosition());
        }

        @OnClick(R.id.editBtn)
        public void clickEditDish() {
            cartChangeListener.editDish(getAdapterPosition());
        }

    }
}
