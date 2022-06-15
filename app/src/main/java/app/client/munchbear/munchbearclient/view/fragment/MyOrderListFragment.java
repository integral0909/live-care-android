package app.client.munchbear.munchbearclient.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.order.created.CreatedOrder;
import app.client.munchbear.munchbearclient.model.reservation.CreatedReservation;
import app.client.munchbear.munchbearclient.model.reservation.Meta;
import app.client.munchbear.munchbearclient.model.response.MyOrdersResponse;
import app.client.munchbear.munchbearclient.model.response.MyReservationResponse;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.view.YourOrderActivity;
import app.client.munchbear.munchbearclient.view.YourReservationActivity;
import app.client.munchbear.munchbearclient.view.adapter.MyOrderAdapter;
import app.client.munchbear.munchbearclient.view.listener.EndlessRecyclerViewScrollListener;
import app.client.munchbear.munchbearclient.viewmodel.MainActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static app.client.munchbear.munchbearclient.model.order.make.MakeOrder.ORDER_ID;

public class MyOrderListFragment extends BaseFragment implements MyOrderAdapter.ItemClickListener{

    @BindView(R.id.ordersRV) RecyclerView ordersRV;

    public static final String KEY_IS_RESERVATION = "key.is.reservation";
    public static final String FIRST_PAGIN_POSTFIX = "1";
    public static final int REQUEST_CODE_RES_CHANGED = 200;
    public static final int RESULT_OK = -1;

    private Unbinder unbinder;
    private MyOrderAdapter orderAdapter;
    private ArrayList<CreatedOrder> orderList = new ArrayList<>();
    private ArrayList<CreatedReservation> createdReservationList = new ArrayList<>();
    private MainActivityViewModel viewModel;
    private Meta paginMetaReservation;
    private Meta paginMetaOrder;
    private boolean isReservation;
    private int clickedPosition;
    private boolean isLoading = false;

    public static MyOrderListFragment newInstance(boolean isReservation) {
        MyOrderListFragment fragment = new MyOrderListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_RESERVATION, isReservation);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        isReservation = bundle.getBoolean(KEY_IS_RESERVATION);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        getBundleData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_orders_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        initList();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObservers();
        loadData();
    }

    private void loadData() {
        if (isReservation) {
            viewModel.getMyReservationList(FIRST_PAGIN_POSTFIX);
        } else {
            viewModel.getMyOrdersList(FIRST_PAGIN_POSTFIX);
        }
    }

    private void initObservers() {
        viewModel.getMyReservationLiveData().observe(getActivity(), myReservationResponse -> handleMyReservationResponse(myReservationResponse));
        viewModel.getMyOrdersResponseLiveData().observe(getActivity(), myOrdersResponse -> handleMyOrderResponse(myOrdersResponse));
        viewModel.getMyReservationErrorLiveData().observe(getActivity(), error -> setupAdapterLastPage());
        viewModel.getMyOrdersResponseErrorLiveData().observe(getActivity(), error -> setupAdapterLastPage());
    }

    private void handleMyReservationResponse(MyReservationResponse myReservationResponse) {
        initList();
        createdReservationList.addAll(myReservationResponse.getCreatedReservationList());
        orderAdapter.notifyDataSetChanged();
        if (myReservationResponse.getMeta() != null) {
            paginMetaReservation = myReservationResponse.getMeta();
        }
        setupAdapterLastPage();
    }

    private void handleMyOrderResponse(MyOrdersResponse myOrdersResponse) {
        initList();
        orderList.addAll(myOrdersResponse.getCreatedOrder());
        orderAdapter.notifyDataSetChanged();
        if (myOrdersResponse.getMeta() != null) {
            paginMetaOrder = myOrdersResponse.getMeta();
        }
        setupAdapterLastPage();
    }

    private void setupAdapterLastPage() {
        isLoading = false;
        if (orderAdapter != null) {
            if (isReservation) {
                orderAdapter.setReservationLastPage(!needMoreData());
            } else {
                orderAdapter.setOrderLastPage(!needMoreData());
            }
        }
    }

    private void initList() {
        if (orderAdapter == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            orderAdapter = new MyOrderAdapter(getContext(), orderList, createdReservationList, this, isReservation);
            ordersRV.setLayoutManager(layoutManager);
            ordersRV.setItemAnimator(new DefaultItemAnimator());
            ordersRV.setAdapter(orderAdapter);
            orderAdapter.setOrderLastPage(true);
            orderAdapter.setReservationLastPage(true);

            ordersRV.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                protected void loadMoreItems() {
                    loadMoreData(isReservation);
                }

                @Override
                public int getTotalPageCount() {
                    if (isReservation) {
                        return paginMetaReservation != null ? paginMetaReservation.getLastPage() : 1000;
                    } else {
                        return paginMetaOrder != null ? paginMetaOrder.getLastPage() : 1000;
                    }
                }

                @Override
                public boolean isLastPage() {
                    if (isReservation) {
                        return paginMetaReservation != null && !needMoreData();
                    } else {
                        return paginMetaOrder != null && !needMoreData();
                    }
                }

                @Override
                public boolean isLoading() {
                    return isLoading;
                }
            });
        }
    }

    private void loadMoreData(boolean isReservation) {
        if (!isLoading && needMoreData() && NetworkUtils.isConnectionAvailable(getContext())) {
            isLoading = true;
            orderAdapter.setReservationLastPage(!needMoreData());

            if (isReservation) {
                viewModel.getMyReservationList(getNextPage(paginMetaReservation));
            } else {
                viewModel.getMyOrdersList(getNextPage(paginMetaOrder));
            }
        }
    }

    private boolean needMoreData() {
        if (isReservation) {
            return (paginMetaReservation != null && !(paginMetaReservation.getCurrentPage() == paginMetaReservation.getLastPage()));
        } else {
            return (paginMetaOrder != null && !(paginMetaOrder.getCurrentPage() == paginMetaOrder.getLastPage()));
        }
    }

    private String getNextPage(Meta paginMeta) {
        return String.valueOf(paginMeta.getCurrentPage() + 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_RES_CHANGED:
                    if (data.hasExtra(CreatedReservation.KEY_CREATED_RESERVATION)) {
                        CreatedReservation createdReservation = data.getParcelableExtra(CreatedReservation.KEY_CREATED_RESERVATION);
                        createdReservationList.set(clickedPosition, createdReservation);
                        orderAdapter.notifyItemChanged(clickedPosition, createdReservation);
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent;
        clickedPosition = position;
        if (isReservation) {
            CreatedReservation createdReservation = createdReservationList.get(position);
            intent = new Intent(getContext(), YourReservationActivity.class);
            intent.putExtra(CreatedReservation.KEY_CREATED_RESERVATION, createdReservation);
            startActivityForResult(intent, REQUEST_CODE_RES_CHANGED);
        } else {
            intent = new Intent(getContext(), YourOrderActivity.class);
            intent.putExtra(ORDER_ID, orderList.get(position).getId());
            startActivity(intent);
        }
    }
}
