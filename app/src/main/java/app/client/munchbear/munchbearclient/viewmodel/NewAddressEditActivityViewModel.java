package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.utils.GoogleMapHelper;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.disposables.CompositeDisposable;

public class NewAddressEditActivityViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<Boolean> saveAddressLiveData = new MutableLiveData<>();
    private MutableLiveData<Address> returnAddressLiveData = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void saveAddress(String city, String country, String street, String house, String unit, String zipCode, double lat, double lng) {
        Address address = new Address(street, house, unit, zipCode, city, "", country, lat, lng);//TODO If will be need add country
        CorePreferencesImpl.getCorePreferences().setDefaultDeliveryAddress(address);
        saveAddressLiveData.postValue(true);
    }

    public void returnNewAddress(String city, String country, String street, String house, String unit, String zipCode, double lat, double lng) {
        Address address = new Address(street, house, unit, zipCode, city, "", country, lat, lng);//TODO If will be need add country
        returnAddressLiveData.postValue(address);
    }

    public void saveAddressForCurrentOrder(Context context, String city, String country, String street, String house, String unit, String zipCode) {
        LatLng userLatLng = GoogleMapHelper.getLatLng(context, country + " " + city + " " + street + " " + house);
        if (userLatLng != null) {
            Address address = new Address(street, house, unit, zipCode, city, "", country, userLatLng.latitude, userLatLng.longitude);
            Cart.getInstance().setSelectedDeliveryAddress(address);
            saveAddressLiveData.setValue(true);
        }
    }

    public MutableLiveData<Boolean> getSaveAddressLiveData() {
        return saveAddressLiveData;
    }

    public MutableLiveData<Address> getReturnAddressLiveData() {
        return returnAddressLiveData;
    }
}
