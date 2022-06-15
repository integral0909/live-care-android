package app.client.munchbear.munchbearclient.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.DishChangeSumListener;
import app.client.munchbear.munchbearclient.interfaces.DishEventsListener;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.utils.ImageUtils;
import app.client.munchbear.munchbearclient.utils.LoyaltyProgram;
import app.client.munchbear.munchbearclient.utils.SquareImageView;
import app.client.munchbear.munchbearclient.utils.TextUtils;
import app.client.munchbear.munchbearclient.view.adapter.DishSizeAdapter;
import app.client.munchbear.munchbearclient.view.adapter.MandatoryCategoryAdapter;
import app.client.munchbear.munchbearclient.view.adapter.ModifierPageAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import xyz.santeri.wvp.WrappingViewPager;

public class DetailDishSelectionFragment extends BaseFragment implements DishChangeSumListener{

    @BindView(R.id.sizeRV)
    RecyclerView sizeRV;
    @BindView(R.id.mandatoryModifierRV)
    RecyclerView mandatoryModifierRV;
    @BindView(R.id.dishCount)
    TextView dishCountTxt;
    @BindView(R.id.modifierSmartTab)
    SmartTabLayout modifierSmartTab;
    @BindView(R.id.modifierViewPager)
    WrappingViewPager modifierViewPager;
    @BindView(R.id.cartBtn)
    ConstraintLayout cartBtn;

    @BindView(R.id.dishImage)
    SquareImageView dishImage;
    @BindView(R.id.dishName)
    TextView dishName;
    @BindView(R.id.description)
    TextView dishDescription;
    @BindView(R.id.modifierLayout)
    LinearLayout modifierLayout;

    @BindView(R.id.orderTotalDollar)
    TextView orderTotalDollar;
    @BindView(R.id.orderTotalPoint)
    TextView orderTotalPoint;
    @BindView(R.id.addToCartTxt)
    TextView addToCartTxt;
    @BindView(R.id.orTxt)
    TextView orTxt;
    @BindView(R.id.imageStar)
    ImageView imageStar;

    private Unbinder unbinder;
    private DishSizeAdapter dishSizeAdapter;
    private MandatoryCategoryAdapter mandatoryCategoryAdapter;
    private ModifierPageAdapter modifierPageAdapter;
    private Dish dish;
    private int dishCount = 1;
    private boolean editDish;
    private int editDishPosition;
    private DishEventsListener dishEventsListener;

    public static DetailDishSelectionFragment newInstance(Dish dish, boolean editDish, int position) {
        DetailDishSelectionFragment fragment = new DetailDishSelectionFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Dish.KEY_DISH, dish);
        bundle.putBoolean(Dish.KEY_DISH_EDIT, editDish);
        bundle.putInt(Dish.KEY_DISH_EDIT_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dishEventsListener = (DishEventsListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDataFromArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_dish_selection_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        initObservers();

        dishCountTxt.setText(String.valueOf(dishCount));
        setupMainViews();
        LoyaltyProgram.setupCartViews(orTxt, imageStar, orderTotalPoint, orderTotalDollar);
        createAdapters();
        initViewPage();
        initSizeList();
        initMandatoryList();

        return view;
    }

    private void initObservers() {

    }

    private void getDataFromArguments() {
        if (getArguments() != null && getArguments().containsKey(Dish.KEY_DISH)) {
            Dish tempDish = getArguments().getParcelable(Dish.KEY_DISH);
            if (tempDish != null) {
                dish = Dish.clone(tempDish);
                dishCount = dish.getCount();
            }
            editDish = getArguments().getBoolean(Dish.KEY_DISH_EDIT);
            editDishPosition = getArguments().getInt(Dish.KEY_DISH_EDIT_POSITION);
        }
    }

    private void setupMainViews() {
        ImageUtils.setImageFromUrl(dish.getImgUrl(), dishImage, R.mipmap.dish_default_avatart);
        dishName.setText(dish.getName());
        dishDescription.setText(dish.getDescription());
        addToCartTxt.setText(editDish ? getResources().getString(R.string.detail_dish_edit) : getResources().getString(R.string.detail_dish_add_to_cart));
        setupTotalPrice();
    }

    private void initSizeList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        sizeRV.setLayoutManager(layoutManager);
        sizeRV.setItemAnimator(new DefaultItemAnimator());
        sizeRV.setAdapter(dishSizeAdapter);
    }

    private void initMandatoryList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mandatoryModifierRV.setLayoutManager(layoutManager);
        mandatoryModifierRV.setItemAnimator(new DefaultItemAnimator());
        mandatoryModifierRV.setAdapter(mandatoryCategoryAdapter);
    }

    private void createAdapters() {
        dishSizeAdapter = new DishSizeAdapter(getContext(), dish.getDishSizeList(), this);
        mandatoryCategoryAdapter = new MandatoryCategoryAdapter(getContext(), dish.getMandatoryModifierList(),
                this);
    }

    private void initViewPage() {
        modifierLayout.setVisibility(hasDishModifiers() ? View.VISIBLE : View.GONE);
        modifierViewPager.setAnimationDuration(100);
        modifierPageAdapter = new ModifierPageAdapter(this.getFragmentManager(), dish.getOptionalModifierList(),
                dish.getAllExcludeInOneList(getContext()), this);
        modifierViewPager.setAdapter(modifierPageAdapter);
        modifierSmartTab.setViewPager(modifierViewPager);
    }

    private boolean hasDishModifiers() {
        boolean hasOptionalModifiers = dish.getOptionalModifierList() != null && dish.getOptionalModifierList().size() > 0;
        boolean hasExcludeModifiers = dish.getExcludeModifierList() != null && dish.getExcludeModifierList().size() > 0;

        return hasOptionalModifiers || hasExcludeModifiers;
    }

    private void setupTotalPrice() {
        TextUtils.setupTotalPrice(getContext(), orderTotalDollar, orderTotalPoint, dish.getCostDollar(),
                dish.getCostPoint(), dishCount);
    }

    @OnClick(R.id.closeBtn)
    public void clickCloseBtn() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.cartBtn)
    public void clickCartBtn() {
        if (editDish) {
            dishEventsListener.onDishEdited(dish, editDishPosition);
        } else {
            dishEventsListener.onDishAdded(dish);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_plus)
    public void addDishCount() {
        dishCount++;
        changeDishCount();
    }

    @OnClick(R.id.btn_minus)
    public void subDishCount() {
        if (dishCount > 1) {
            dishCount--;
            changeDishCount();
        }
    }

    private void changeDishCount() {
        dishCountTxt.setText(String.valueOf(dishCount));
        dish.setCount(dishCount);

        setupTotalPrice();
    }

    @Override
    public void onChangeTotalSum() {
        setupTotalPrice();
    }
}
