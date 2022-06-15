package app.client.munchbear.munchbearclient.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.restaurant.AvailableRestaurants;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.view.NewAddressEditActivity;
import app.client.munchbear.munchbearclient.view.SelectLocationActivity;
import app.client.munchbear.munchbearclient.viewmodel.PreMenuActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/*
 * @author Nazar V.
 */
public class PreMenuFragment extends BaseFragment {

    public static final int TYPE_NEW_DELIVERY_ADDRESS = 1;
    public static final int TYPE_SELECT_RESTAURANT_ADDRESS = 2;
    public static final String MENU_OPEN_TYPE = "menu.open.type";

    @BindView(R.id.preMenuCardFirst)
    CardView cardViewFirst;

    @BindView(R.id.preMenuCardSecond)
    CardView cardViewSecond;

    @BindView(R.id.preMenuCardThird)
    CardView cardViewThird;

    private Unbinder unbinder;
    private PreMenuActivityViewModel preMenuActivityViewModel;

    public static PreMenuFragment newInstance() {
        PreMenuFragment preMenuFragment = new PreMenuFragment();
        return preMenuFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pre_menu_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        preMenuActivityViewModel = ViewModelProviders.of(getActivity()).get(PreMenuActivityViewModel.class);

        setupViews();
        initObservers();
        return view;
    }

    @OnClick(R.id.preMenuCardFirst)
    public void onFirstCardClick(View v) {
        if (checkButtonBeforeClick(true)) {
            preMenuActivityViewModel.handleNavigateMenuFragmentClick(TYPE_NEW_DELIVERY_ADDRESS, DeliveryVariant.TYPE_DELIVERY);
        }
    }

    @OnClick(R.id.preMenuCardSecond)
    public void onSecondCardClick(View v) {
        if (checkButtonBeforeClick(false)) {
            preMenuActivityViewModel.handleNavigateMenuFragmentClick(TYPE_SELECT_RESTAURANT_ADDRESS, DeliveryVariant.TYPE_PICK_UP);
        }
    }

    @OnClick(R.id.preMenuCardThird)
    public void onThirdCardClick(View v) {
        if (checkButtonBeforeClick(false)) {
            preMenuActivityViewModel.handleNavigateMenuFragmentClick(TYPE_SELECT_RESTAURANT_ADDRESS, DeliveryVariant.TYPE_EAT_IN);
        }
    }

    private boolean checkButtonBeforeClick(boolean isDelivery) {
        return NetworkUtils.isConnectionAvailable(getContext())
                && AvailableRestaurants.getInstance().isRestaurantListEmpty(getContext(), isDelivery);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void setupViews() {
        Resources resources = getResources();
        setupCustomTitles(resources);
        setupCustomExplanations(resources);
        setCustomImages(resources);
    }

    private void setupCustomTitles(Resources resources) {
        TextView tvFirstTitle = cardViewFirst.findViewById(R.id.preMenuCardTvTitle);
        TextView tvSecondTitle = cardViewSecond.findViewById(R.id.preMenuCardTvTitle);
        TextView tvThirdTitle = cardViewThird.findViewById(R.id.preMenuCardTvTitle);
        tvFirstTitle.setText(resources.getString(R.string.pre_menu_first_type_title));
        tvSecondTitle.setText(resources.getString(R.string.pre_menu_second_type_title));
        tvThirdTitle.setText(resources.getString(R.string.pre_menu_third_type_title));
    }

    private void setupCustomExplanations(Resources resources) {
        TextView tvFirstExplanation = cardViewFirst.findViewById(R.id.preMenuCardTvExplanation);
        TextView tvSecondExplanation = cardViewSecond.findViewById(R.id.preMenuCardTvExplanation);
        TextView tvThirdExplanation = cardViewThird.findViewById(R.id.preMenuCardTvExplanation);
        tvFirstExplanation.setText(resources.getString(R.string.pre_menu_first_type_explanation));
        tvSecondExplanation.setText(resources.getString(R.string.pre_menu_second_type_explanation));
        tvThirdExplanation.setText(resources.getString(R.string.pre_menu_third_type_explanation));
    }

    private void setCustomImages(Resources resources) {
        ImageView tvFirsImage = cardViewFirst.findViewById(R.id.preMenuCardIv);
        ImageView tvSecondImage = cardViewSecond.findViewById(R.id.preMenuCardIv);
        ImageView tvThirdImage = cardViewThird.findViewById(R.id.preMenuCardIv);
        tvFirsImage.setImageDrawable(resources.getDrawable(R.mipmap.ic_delivery));
        tvSecondImage.setImageDrawable(resources.getDrawable(R.mipmap.ic_pick_up));
        tvThirdImage.setImageDrawable(resources.getDrawable(R.mipmap.ic_eat_in));
    }

    private void initObservers() {
        /* this observer should be notified to navigate only when deliver and pickup address were set*/
        preMenuActivityViewModel.getNavigateMenuFragment().observe(this, type -> finishPreMenuActivity(type));
    }

    private void finishPreMenuActivity(int type) {
        Bundle bundle = new Bundle();
        if (type == TYPE_NEW_DELIVERY_ADDRESS) {
            bundle.putInt(MENU_OPEN_TYPE, TYPE_NEW_DELIVERY_ADDRESS);
            openActivity(getContext(), NewAddressEditActivity.class, bundle);
        } else {
            bundle.putInt(MENU_OPEN_TYPE, TYPE_SELECT_RESTAURANT_ADDRESS);
            openActivity(getContext(), SelectLocationActivity.class, bundle);
        }
    }
}
