package app.client.munchbear.munchbearclient.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.view.NewAddressEditActivity;
import app.client.munchbear.munchbearclient.view.adapter.DeliveryAddressAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DeliveryAddressFragment extends BaseFragment implements DeliveryAddressAdapter.ChangeAddressListListener{

    @BindView(R.id.previousDeliveryRV)
    RecyclerView previousDeliveryRV;
    @BindView(R.id.previousDeliveryTitle)
    TextView previousDeliveryTitle;
    @BindView(R.id.addAddressBtn)
    TextView addAddressBtn;

    public static final String KEY_IS_FROM_PROFILE = "key.is.from.profile";

    private Unbinder unbinder;
    private List<Address> addressList = new ArrayList<>();
    private DeliveryAddressAdapter addressAdapter;
    private boolean fromProfile;

    public static DeliveryAddressFragment newInstance(boolean fromProfile) {
        DeliveryAddressFragment fragment = new DeliveryAddressFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_FROM_PROFILE, fromProfile);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDataFromArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delivery_address_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        checkDeliveryList();

        return view;
    }

    private void getDataFromArguments() {
        Bundle bundle = getArguments();
        fromProfile = bundle.getBoolean(KEY_IS_FROM_PROFILE);
    }

    private void checkDeliveryList() {
        addAddressBtn.setVisibility(fromProfile ? View.VISIBLE : View.GONE);
        addressList = CorePreferencesImpl.getCorePreferences().getPreviousDeliveryAddressList();
        previousDeliveryTitle.setVisibility(addressList == null || addressList.size() == 0 ? View.GONE : View.VISIBLE);

        if (addressList != null && addressList.size() > 0) {
            initDeliveryAddressList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.addAddressBtn)
    public void clickAddAddress() {
        startActivityForResult(new Intent(getContext(), NewAddressEditActivity.class), NewAddressEditActivity.NEW_ADDRESS_REQUEST_CODE);
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        getActivity().onBackPressed();
    }

    private void initDeliveryAddressList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        addressAdapter = new DeliveryAddressAdapter(getContext(), addressList, this, fromProfile);
        previousDeliveryRV.setLayoutManager(layoutManager);
        previousDeliveryRV.setItemAnimator(new DefaultItemAnimator());
        previousDeliveryRV.setAdapter(addressAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case NewAddressEditActivity.NEW_ADDRESS_REQUEST_CODE:
                    checkDeliveryList();
                    break;
            }
        }
    }

    @Override
    public void hideListTitle() {
        previousDeliveryTitle.setVisibility(View.GONE);
    }

    @Override
    public void finishFragment() {
        ((NewAddressEditActivity)getActivity()).onSelectedAddress();
    }
}
