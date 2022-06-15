package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.CartChangeListener;
import app.client.munchbear.munchbearclient.interfaces.DishEventsListener;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import app.client.munchbear.munchbearclient.utils.LoyaltyProgram;
import app.client.munchbear.munchbearclient.utils.TextUtils;
import app.client.munchbear.munchbearclient.view.adapter.CartDishAdapter;
import app.client.munchbear.munchbearclient.view.fragment.DetailDishSelectionFragment;
import app.client.munchbear.munchbearclient.viewmodel.CartActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CartActivity extends BaseActivity implements CartChangeListener, DishEventsListener{

    @BindView(R.id.cartItemRV) RecyclerView cartItemRV;
    @BindView(R.id.cartTotalDollar) TextView cartTotalDollar;
    @BindView(R.id.cartTotalPoint) TextView cartTotalPoint;
    @BindView(R.id.container) FrameLayout container;
    @BindView(R.id.transparentBackground) FrameLayout transparentBackground;
    @BindView(R.id.imageStar) ImageView imageStar;
    @BindView(R.id.orTxt) TextView orTxt;

    private CartActivityViewModel cartActivityViewModel;
    private CartDishAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        cartActivityViewModel = ViewModelProviders.of(this).get(CartActivityViewModel.class);

        initObservers();
        setupViews();
        LoyaltyProgram.setupCartViews(orTxt, imageStar, cartTotalPoint, cartTotalDollar);
    }

    private void initObservers() {
        cartActivityViewModel.getDishEditLiveData().observe(this, edited -> {
            setupViews();
            onBackPressed();
        });

        cartActivityViewModel.getTotalSumValidLiveData().observe(this, isValid -> openActivity(CheckoutActivity.class, null));
    }

    private void setupViews() {
        initCartItemRV();
        initTotalSumViews();
    }

    private void initTotalSumViews() {
        TextUtils.setupTotalPrice(this, cartTotalDollar, cartTotalPoint, Cart.getInstance().getTotalPriceDollar(),
                Cart.getInstance().getTotalPricePoint(), 1);
    }

    private void initCartItemRV() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        itemAdapter = new CartDishAdapter(this, true, Cart.getInstance().getDishList(), this);
        cartItemRV.setLayoutManager(layoutManager);
        cartItemRV.setItemAnimator(new DefaultItemAnimator());
        cartItemRV.setAdapter(itemAdapter);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentByTag(DetailDishSelectionFragment.class.getName());
        if (fragment instanceof DetailDishSelectionFragment) {
            transparentBackground.setVisibility(View.GONE);
        }
        super.onBackPressed();
    }

    @Override
    public void deleteDish() {
        initTotalSumViews();
    }

    @Override
    public void clearCart() {
        finish();
    }

    @Override
    public void editDish(int position) {
        transparentBackground.setVisibility(View.VISIBLE);
        replaceFragment(DetailDishSelectionFragment.newInstance(Cart.getInstance().getDishList().get(position), true, position),
                true, R.id.container, true, true);
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        onBackPressed();
    }

    @OnClick(R.id.checkoutBtn)
    public void checkout() {
        if (Cart.getInstance().getSelectedDeliveryType() == DeliveryVariant.TYPE_DELIVERY) {
            cartActivityViewModel.checkCartTotalPrice(this);
        } else {
            openActivity(CheckoutActivity.class, null);
        }
    }

    @Override
    public void onDishAdded(Dish dish) {

    }

    @Override
    public void onDishEdited(Dish dish, int position) {
        transparentBackground.setVisibility(View.GONE);
        cartActivityViewModel.editDish(dish, position);
    }
}
