package app.client.munchbear.munchbearclient.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.order.created.CreatedOrder;
import app.client.munchbear.munchbearclient.model.reservation.CreatedReservation;
import app.client.munchbear.munchbearclient.model.response.MyOrdersResponse;
import app.client.munchbear.munchbearclient.model.response.MyReservationResponse;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.view.adapter.MyOrdersPageAdapter;
import app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrdersFragment extends BaseFragment {

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) SmartTabLayout tabLayout;

    private int TAB_MY_ORDERS = 0;
    private int TAB_MY_RESERVATION = 1;

    private MainActivityViewModel mainActivityViewModel;
    private Unbinder unbinder;
    private MyOrdersPageAdapter ordersPageAdapter;

    public static MyOrdersFragment newInstance() {
        MyOrdersFragment fragment = new MyOrdersFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_orders_tab_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        initViewPager();
        initObservers();

        return view;
    }

    private void initObservers() {
        mainActivityViewModel.getMyReservationErrorLiveData().observe(getActivity(), error -> Toast.makeText(getActivity(), getResources().getString(R.string.toast_reservation_list_error), Toast.LENGTH_SHORT).show());
        mainActivityViewModel.getMyOrdersResponseErrorLiveData().observe(getActivity(), error -> Toast.makeText(getActivity(), getResources().getString(R.string.toast_order_list_error), Toast.LENGTH_SHORT).show());
    }

    private void initViewPager() {
        ordersPageAdapter = new MyOrdersPageAdapter(this.getFragmentManager(), getContext());
        viewPager.setAdapter(ordersPageAdapter);
        tabLayout.setViewPager(viewPager);
        setupViewPagerPage();
    }

    private void setupViewPagerPage() {
        boolean openReservationTab = CorePreferencesImpl.getCorePreferences().isNeedOpenReservationTab();
        viewPager.setCurrentItem(openReservationTab ? TAB_MY_RESERVATION : TAB_MY_ORDERS);
        CorePreferencesImpl.getCorePreferences().setNeedOpenReservationTab(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
