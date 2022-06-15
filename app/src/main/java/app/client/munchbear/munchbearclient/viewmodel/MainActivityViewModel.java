package app.client.munchbear.munchbearclient.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import app.client.munchbear.munchbearclient.model.payment.CreditCard;
import app.client.munchbear.munchbearclient.model.request.FCMTokenRequest;
import app.client.munchbear.munchbearclient.model.reservation.CreatedReservation;
import app.client.munchbear.munchbearclient.model.reservation.Reservation;
import app.client.munchbear.munchbearclient.model.response.GetUserDataResponse;
import app.client.munchbear.munchbearclient.model.response.MyOrdersResponse;
import app.client.munchbear.munchbearclient.model.response.MyReservationResponse;
import app.client.munchbear.munchbearclient.model.restaurant.AvailableRestaurants;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.model.dish.DishCategory;
import app.client.munchbear.munchbearclient.model.response.RestaurantMenuResponse;
import app.client.munchbear.munchbearclient.model.store.ApiClient;
import app.client.munchbear.munchbearclient.model.user.UserAvatar;
import app.client.munchbear.munchbearclient.model.user.UserData;
import app.client.munchbear.munchbearclient.utils.DishHelper;
import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;
import app.client.munchbear.munchbearclient.model.response.RestaurantListResponse;
import app.client.munchbear.munchbearclient.utils.GoogleLoginHelper;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.firebase.RegisterFirebaseTokenWorker;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferences;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by Roman on 7/5/2018.
 */
public class MainActivityViewModel extends ProfileViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    //MainActivity live data
    private MutableLiveData<Boolean> logoutLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> openDeliveryAddressLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> openPaymentMethodLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> showPromoCodeFragmentLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> showPreMenuLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> openAddCardFragment = new MutableLiveData<>();
    private MutableLiveData<CreditCard> openEditCreditCard = new MutableLiveData<>();

    //MenuFragment live data
    private MutableLiveData<Dish> showSubMenuDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> showMenuLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> cartChangesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<DishCategory>> dishCategoryLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> dishCategoryErrorLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Dish>> allDishListMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> dishCategoryIdsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearCartViewLiveData = new MutableLiveData<>();

    //Restaurant menu
    private MutableLiveData<Boolean> restaurantListErrorLiveData = new MutableLiveData<>();

    //My Reservation
    private MutableLiveData<MyReservationResponse> myReservationLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> myReservationErrorLiveData = new MutableLiveData<>();

    //My Orders
    private MutableLiveData<MyOrdersResponse> myOrdersResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> myOrdersResponseErrorLiveData = new MutableLiveData<>();

    //Refresh list of orders or reservations from push
    private MutableLiveData<Boolean> refreshReservationsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> refreshOrderLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> refreshCardListLiveData = new MutableLiveData<>();

    public static final String FIREBASE_TOKEN = "firebase.token";

    @Override
    protected void onCleared() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void logout() {
        CorePreferencesImpl corePref = CorePreferencesImpl.getCorePreferences();

        Cart.clearCart();
        logoutFromSocNet(corePref);

        corePref.setLoginType(LoginType.NOT_LOGIN);
        corePref.setFirstStart(true);
        corePref.setPreviousDeliveryAddressList(null);
        corePref.setUserData(null);
        corePref.setAccessToken("");
        corePref.setRefreshToken("");
        corePref.setExpiresToken(0);
        corePref.setUserHasAvatar(false);
        UserAvatar.deleteUserAvatar();

        logoutLiveData.setValue(true);
    }

    public void logoutFromSocNet(CorePreferences corePreferences) {
        switch (corePreferences.getLoginType()) {
            case LoginType.FACEBOOK:
                LoginManager.getInstance().logOut();
                break;
            case LoginType.GOOGLE:
                GoogleLoginHelper.logout();
                break;
        }
    }

    public void initRestaurantMenu(int restaurantId, boolean loyaltyProgramEnable, boolean needRefresh) {
        CorePreferencesImpl.getCorePreferences().setSelectedRestaurantId(restaurantId);
        CorePreferencesImpl.getCorePreferences().enableLoyalty(loyaltyProgramEnable);

        ApiClient.getRepository().getRestaurantMenu(restaurantId, CorePreferencesImpl.getCorePreferences().getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<RestaurantMenuResponse>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        compositeDisposable.add(disposable);
                    }

                    @Override
                    public void onSuccess(Response<RestaurantMenuResponse> menuResponse) {
                        if (menuResponse.isSuccessful()) {
                            if (menuResponse.body() != null && menuResponse.body().getRestaurantMenu() != null) {
                                List<DishCategory> dishCategoryList = menuResponse.body().getRestaurantMenu().getDishCategoryList();
                                DishHelper.initFirstState(dishCategoryList);
                                CorePreferencesImpl.getCorePreferences().setSelectedRestaurantCategory(dishCategoryList);
                                handleMenuResponse(dishCategoryList, needRefresh);
                            }
                        } else {
                            dishCategoryErrorLiveData.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        dishCategoryErrorLiveData.setValue(true);
                    }
                });
    }

    public void onMenuSearchCancelBtnClick() {
        allDishListMutableLiveData.setValue(allDishListMutableLiveData.getValue());
    }

    public void handleMenuResponse(List<DishCategory> dishCategoryList, boolean needRefresh) {
        AvailableRestaurants.getInstance().setNeedRefresh(!needRefresh);
        dishCategoryLiveData.setValue(dishCategoryList);
        dishCategoryIdsMutableLiveData.setValue(getCategoryIdsList(dishCategoryList));
        getAllDish();
    }

    public List<Dish> getDishListByCategoryId(int id) {
        List<DishCategory> dishCategories = dishCategoryLiveData.getValue();
        for (DishCategory dishCategory : dishCategories) {
            if (dishCategory.getId() == id) {
                return dishCategory.getDishList();
            }
        }

        return new ArrayList<>();
    }

    public void openDeliveryAddress() {
        openDeliveryAddressLiveData.setValue(true);
    }

    public void openPaymentMethod() {
        openPaymentMethodLiveData.setValue(true);
    }

    public void openPreMenu() {
        showPreMenuLiveData.setValue(true);
    }

    public void addDishToCart(Dish dish) {
        double cartTotalDollar = Cart.getInstance().getTotalPriceDollar();
        int cartTotalPoint = Cart.getInstance().getTotalPricePoint();

        Cart.getInstance().setCartEmpty(false);
        Cart.getInstance().setTotalPriceDollar(cartTotalDollar + (dish.getCostDollar() * dish.getCount()));
        Cart.getInstance().setTotalPricePoint(cartTotalPoint + (dish.getCostPoint() * dish.getCount()));

        List<Dish> dishList = Cart.getInstance().getDishList();
        if (dishList == null) {
            dishList = new ArrayList<>();
        }
        dishList.add(dish);

        Cart.getInstance().setDishList(dishList);
        cartChangesLiveData.setValue(true);
    }

    public void clearCartView() {
        clearCartViewLiveData.setValue(true);
    }

    public void handleItemPromoCodeClick(int position) {
        showPromoCodeFragmentLiveData.setValue(position);
    }

    public void handleBtnClick(int position) {
        showMenuLiveData.setValue(position);
    }

    public void handleSubMenuItemClick(Dish dish) {
        showSubMenuDetailsLiveData.setValue(dish);
    }

    private void getAllDish() {
        List<Dish> allDishList = new ArrayList<>();
        for (DishCategory dishCategory : dishCategoryLiveData.getValue()) {
            if (dishCategory.getDishList() != null && dishCategory.getDishList().size() > 0) {
                allDishList.addAll(dishCategory.getDishList());
            }
        }

        allDishListMutableLiveData.setValue(getUniqueDishes(allDishList));
    }

    private List<Dish> getUniqueDishes(List<Dish> allDishList) {
        Set<Dish> dishSet = new HashSet<>(allDishList);
        allDishList = new ArrayList<>(dishSet);
        return allDishList;
    }

    private List<Integer> getCategoryIdsList(List<DishCategory> dishCategoryList) {
        List<Integer> dishCategoryIdsList = new ArrayList<>();
        if (!dishCategoryList.isEmpty()) {
            for (DishCategory category : dishCategoryList) {
                dishCategoryIdsList.add(category.getId());
            }
        }
        return dishCategoryIdsList;
    }

    public void getRestaurantInsideList() {
        ApiClient.getRepository().restaurantInsideList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<RestaurantListResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<RestaurantListResponse> restaurantListResponseResponse) {
                        if (restaurantListResponseResponse.isSuccessful()) {
                            handleRestaurantResponse(restaurantListResponseResponse, false);
                            changeRestaurantForInitMenu();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        restaurantListErrorLiveData.setValue(true);
                    }
                });
    }

    private void changeRestaurantForInitMenu() {
        int savedRestaurantId = AvailableRestaurants.getInstance().getFirstRestaurantId();
        boolean loyaltyEnable = AvailableRestaurants.getInstance().isFirstRestaurantLoyalty();
        initRestaurantMenu(savedRestaurantId, loyaltyEnable, true);
    }

    public void getRestaurantDeliveryList() {
        ApiClient.getRepository().restaurantDeliveryList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<RestaurantListResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<RestaurantListResponse> restaurantListResponseResponse) {
                        handleRestaurantResponse(restaurantListResponseResponse, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        restaurantListErrorLiveData.setValue(true);
                    }
                });
    }

    private void handleRestaurantResponse(Response<RestaurantListResponse> restaurantListResponseResponse, boolean isDelivery) {
        if (restaurantListResponseResponse.isSuccessful()) {
            if (restaurantListResponseResponse.body() != null) {
                List<Restaurant> restaurantList = restaurantListResponseResponse.body().getRestaurantList();
                if (isDelivery) {
                    AvailableRestaurants.getInstance().setDeliveryRestaurantList(restaurantList);
                } else {
                    AvailableRestaurants.getInstance().setBaseRestaurantList(restaurantList);
                    AvailableRestaurants.getInstance().initReservationRestaurantList();
                }
            }
        } else {
            restaurantListErrorLiveData.setValue(true);
        }
    }

    public void getMyReservationList(String paginPostfix) {
        ApiClient.getRepository().getMyReservation(paginPostfix, CorePreferencesImpl.getCorePreferences().getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<MyReservationResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<MyReservationResponse> myReservationResponseResponse) {
                        if (myReservationResponseResponse.isSuccessful()) {
                            if (myReservationResponseResponse.body() != null) {
                                myReservationLiveData.setValue(myReservationResponseResponse.body());
                            }
                        } else {
                            myReservationErrorLiveData.setValue(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        myReservationErrorLiveData.setValue(true);
                    }
                });
    }

    public void handleRefreshingBroadcast(Intent intent) {
        if (intent.getAction().equals(Reservation.ACTION_RESERVATION_UPDATED)) {
            boolean isReservation = intent.getBooleanExtra(Reservation.KEY_IS_RESERVATION, false);

            if (isReservation) {
                refreshReservationsLiveData.setValue(true);
            } else {
                refreshOrderLiveData.setValue(false);
            }
        }
    }

    public void registerFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task ->
                handleOnRefreshTokenCompletedListener(task));
    }

    private void handleOnRefreshTokenCompletedListener(@NonNull Task<InstanceIdResult> task) {
        try {
            InstanceIdResult instanceIdResult = task.getResult();
            if (instanceIdResult != null) {
                String token = instanceIdResult.getToken();
                if (token != null) {
                    Data.Builder builder = new Data.Builder();
                    builder.putString(FIREBASE_TOKEN, token);
                    builder.build();

                    OneTimeWorkRequest work = new OneTimeWorkRequest
                            .Builder(RegisterFirebaseTokenWorker.class)
                            .setInputData(builder.build())
                            .build();
                    WorkManager.getInstance().enqueue(work);
                }
            }
        } catch (Exception e) {

        }
    }

    public void getMyOrdersList(String paginPostfix) {
        ApiClient.getRepository().getOrderList(paginPostfix, CorePreferencesImpl.getCorePreferences().getAccessToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<MyOrdersResponse>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<MyOrdersResponse> myOrdersResponseResponse) {
                        if (myOrdersResponseResponse.isSuccessful()) {
                            myOrdersResponseLiveData.setValue(myOrdersResponseResponse.body());
                        } else {
                            myOrdersResponseErrorLiveData.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        myOrdersResponseErrorLiveData.setValue(false);
                    }
                });
    }

    public void openAddCardFragment() {
        openAddCardFragment.setValue(true);
    }

    public void openEditCreditCard(CreditCard creditCard) {
        openEditCreditCard.setValue(creditCard);
    }

    public void refreshCardList() {
        refreshCardListLiveData.setValue(true);
    }

    //getter methods for live data, keep it at bottom, because it's basically boilerplate code
    public MutableLiveData<Boolean> getLogoutLiveData() {
        return logoutLiveData;
    }

    public MutableLiveData<Boolean> getOpenDeliveryAddressLiveData() {
        return openDeliveryAddressLiveData;
    }

    public MutableLiveData<Dish> getShowSubMenuDetailsLiveData() {
        return showSubMenuDetailsLiveData;
    }

    public MutableLiveData<Boolean> getOpenPaymentMethodLiveData() {
        return openPaymentMethodLiveData;
    }

    public MutableLiveData<Integer> getShowPromoCodeFragmentLiveData() {
        return showPromoCodeFragmentLiveData;
    }

    public MutableLiveData<List<DishCategory>> getDishCategoryLiveData() {
        return dishCategoryLiveData;
    }

    public MutableLiveData<List<Dish>> getAllDishListMutableLiveData() {
        return allDishListMutableLiveData;
    }

    public MutableLiveData<List<Integer>> getDishCategoryIdsMutableLiveData() {
        return dishCategoryIdsMutableLiveData;
    }

    public MutableLiveData<Integer> getShowMenuLiveData() {
        return showMenuLiveData;
    }

    public MutableLiveData<Boolean> getShowPreMenuLiveData() {
        return showPreMenuLiveData;
    }

    public MutableLiveData<Boolean> getCartChangesLiveData() {
        return cartChangesLiveData;
    }

    public MutableLiveData<Boolean> getClearCartViewLiveData() {
        return clearCartViewLiveData;
    }

    public MutableLiveData<Boolean> getRestaurantListErrorLiveData() {
        return restaurantListErrorLiveData;
    }

    public MutableLiveData<MyReservationResponse> getMyReservationLiveData() {
        return myReservationLiveData;
    }

    public MutableLiveData<Boolean> getMyReservationErrorLiveData() {
        return myReservationErrorLiveData;
    }

    public MutableLiveData<Boolean> getRefreshReservationsLiveData() {
        return refreshReservationsLiveData;
    }

    public MutableLiveData<Boolean> getRefreshOrderLiveData() {
        return refreshOrderLiveData;
    }

    public MutableLiveData<Boolean> getDishCategoryErrorLiveData() {
        return dishCategoryErrorLiveData;
    }

    public MutableLiveData<MyOrdersResponse> getMyOrdersResponseLiveData() {
        return myOrdersResponseLiveData;
    }

    public MutableLiveData<Boolean> getMyOrdersResponseErrorLiveData() {
        return myOrdersResponseErrorLiveData;
    }

    public MutableLiveData<Boolean> getOpenAddCardFragment() {
        return openAddCardFragment;
    }

    public MutableLiveData<Boolean> getRefreshCardList() {
        return refreshCardListLiveData;
    }

    public MutableLiveData<CreditCard> getOpenEditCreditCard() {
        return openEditCreditCard;
    }
}
