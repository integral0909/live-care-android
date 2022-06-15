package app.client.munchbear.munchbearclient.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.utils.ImageUtils;
import app.client.munchbear.munchbearclient.view.adapter.MenuAdapter;
import app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SubMenuFragment extends BaseFragment implements MenuAdapter.MenuItemClickListener {

    private static final String SUB_MENU_DISH_CATEGORY_ID = "sub.menu.dish.category.id";

    @BindView(R.id.rvSubMenu)
    RecyclerView rvSubMenu;

    private MainActivityViewModel viewModel;
    private Unbinder unbinder;
    private MenuAdapter menuAdapter;

    public static SubMenuFragment newInstance(int dishCategoryId) {
        SubMenuFragment subMenuFragment = new SubMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SUB_MENU_DISH_CATEGORY_ID, dishCategoryId);
        subMenuFragment.setArguments(bundle);
        return subMenuFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.sub_menu_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        initObservers();
        setupViews();
        getDataFromArgument();
        changeHolderParams(Cart.getInstance().isCartEmpty() ? 0 : 45);
        return view;
    }

    private void initObservers() {
        viewModel.getCartChangesLiveData().observe(this, change -> changeHolderParams(45));
        viewModel.getClearCartViewLiveData().observe(this, clear -> changeHolderParams(Cart.getInstance().isCartEmpty() ? 0 : 45));
    }

    private void changeHolderParams(int bottomMargin) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rvSubMenu.getLayoutParams();
        params.setMargins(0,0,0, ImageUtils.getDpFromPx(getContext(), bottomMargin));
        rvSubMenu.setLayoutParams(params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onClick(Dish dish) {
        viewModel.handleSubMenuItemClick(dish);
    }

    private void getDataFromArgument() {
        int categoryId = getArguments().getInt(SUB_MENU_DISH_CATEGORY_ID, -1);
        if (categoryId != -1) {
            updateAdapter(viewModel.getDishListByCategoryId(categoryId));
        }
    }

    private void setupViews() {
        menuAdapter = new MenuAdapter(getContext(), this);
        rvSubMenu.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvSubMenu.setAdapter(menuAdapter);
    }

    private void updateAdapter(List<Dish> dishList) {
        menuAdapter.refreshAdapter(dishList);
    }
}
