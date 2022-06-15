package app.client.munchbear.munchbearclient.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.dish.DishCategory;
import app.client.munchbear.munchbearclient.model.menu.RestaurantMenu;
import app.client.munchbear.munchbearclient.model.user.Token;
import app.client.munchbear.munchbearclient.model.user.UserData;

/**
 * Created by Roman on 7/5/2018.
 */
public class CorePreferencesImpl implements CorePreferences{

    private static final String CORE_PREFERENCES = "core.preferences.food.genie";
    private static final String FIRST_START = "core.preferences.first.start";
    private static final String LOGIN_TYPE = "core.preferences.login.type";
    private static final String NEED_FORCE_OPEN_MENU = "core.preferences.need.force.open.menu";
    private static final String NEED_FORCE_OPEN_MY_ORDER = "core.preferences.need.force.open.my.orders";
    private static final String USER_DATA = "core.preferences.user.data";
    private static final String PREVIOUS_DELIVERY_ADDRESS_LIST = "core.preferences.previous.delivery.address.list";
    private static final String ACCESS_TOKEN = "core.preferences.access.token";
    private static final String REFRESH_TOKEN = "core.preferences.refresh.token";
    private static final String TOKEN = "core.preferences.token";
    private static final String EXPIRES_TOKEN = "core.preferences.expires.token";
    private static final String SELECTED_RESTAURANT_ID = "selected.restaurant.id";
    private static final String SELECTED_RESTAURANT_CATEGORY = "selected.restaurant.category";
    private static final String NEED_OPEN_RESERVATION_TAB = "need.open.reservation.tab";
    private static final String FCM_TOKEN = "core.preferences.fcm.token";
    private static final String LOYALTY_PROGRAM_ENABLE = "core.preferences.loyalty.program.enable";
    private static final String USER_HAS_AVATAR = "core.preferences.user.has.avatar";

    private static CorePreferencesImpl corePreferences;
    private SharedPreferences sharedPreferences = null;

    private CorePreferencesImpl() {

    }

    public static synchronized CorePreferencesImpl getCorePreferences() {
        if (corePreferences == null) {
            corePreferences = new CorePreferencesImpl();
        }

        return corePreferences;
    }

    public void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CORE_PREFERENCES, Context.MODE_PRIVATE);
        }
    }


    @Override
    public boolean firstStart() {
        return sharedPreferences.getBoolean(FIRST_START, true);
    }

    @Override
    public void setFirstStart(boolean firstStart) {
        sharedPreferences.edit().putBoolean(FIRST_START, firstStart).commit();
    }

    @Override
    public boolean isNeedForceOpenMenu() {
        return sharedPreferences.getBoolean(NEED_FORCE_OPEN_MENU, false);
    }

    @Override
    public void setForceOpenMenu(boolean isNeed) {
        sharedPreferences.edit().putBoolean(NEED_FORCE_OPEN_MENU, isNeed).commit();
    }

    @Override
    public boolean isNeedForceOpenMyOrder() {
        return sharedPreferences.getBoolean(NEED_FORCE_OPEN_MY_ORDER, false);
    }

    @Override
    public void setForceOpenMyOrder(boolean isNeed) {
        sharedPreferences.edit().putBoolean(NEED_FORCE_OPEN_MY_ORDER, isNeed).commit();
    }

    @Override
    public boolean isNeedOpenReservationTab() {
        return sharedPreferences.getBoolean(NEED_OPEN_RESERVATION_TAB, false);
    }

    @Override
    public void setNeedOpenReservationTab(boolean isNeed) {
        sharedPreferences.edit().putBoolean(NEED_OPEN_RESERVATION_TAB, isNeed).commit();
    }

    @Override
    public UserData getUserData() {
        Gson gson = new Gson();
        String userJson = sharedPreferences.getString(USER_DATA, "");
        return gson.fromJson(userJson, UserData.class);
    }

    @Override
    public void setUserData(UserData userDataData) {
        Gson gson = new Gson();
        sharedPreferences.edit().putString(USER_DATA, gson.toJson(userDataData)).commit();
    }

    @Override
    public Address getDefaultDeliveryAddress() {
        List<Address> addressList = getPreviousDeliveryAddressList();
        if (addressList != null && addressList.size() > 0) {
            return addressList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void setDefaultDeliveryAddress(Address address) {
        List<Address> addressList = getPreviousDeliveryAddressList();
        if (addressList == null) {
            addressList = new ArrayList<>();
        }
        addressList.add(0, address);
        setPreviousDeliveryAddressList(addressList);
    }

    @Override
    public List<Address> getPreviousDeliveryAddressList() {
        Gson gson = new Gson();
        String addressJson = sharedPreferences.getString(PREVIOUS_DELIVERY_ADDRESS_LIST, "");
        Type type = new TypeToken<List<Address>>(){}.getType();
        return gson.fromJson(addressJson, type);
    }

    @Override
    public void setPreviousDeliveryAddressList(List<Address> addressList) {
        Gson gson = new Gson();
        sharedPreferences.edit().putString(PREVIOUS_DELIVERY_ADDRESS_LIST, gson.toJson(addressList)).commit();
    }

    @Override
    public String getAccessToken() {
        String accessToken = "Bearer " + sharedPreferences.getString(ACCESS_TOKEN, "");
        return accessToken;
    }

    @Override
    public void setAccessToken(String accessToken) {
        sharedPreferences.edit().putString(ACCESS_TOKEN, accessToken).commit();
    }

    @Override
    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN, "");
    }

    @Override
    public void setRefreshToken(String refreshToken) {
        sharedPreferences.edit().putString(REFRESH_TOKEN, refreshToken).commit();
    }

    @Override
    public long getExpiresToken() {
        return sharedPreferences.getLong(EXPIRES_TOKEN, 3600);
    }

    @Override
    public void setExpiresToken(long expiresToken) {
        // Access token will be not valid after 1h, but I refresh it after 50m
        Long currentTime = System.currentTimeMillis();
        expiresToken = currentTime + (expiresToken - 600) * 1000L;
        sharedPreferences.edit().putLong(EXPIRES_TOKEN, expiresToken).commit();
    }

    @Override
    public String getToken() {
        return sharedPreferences.getString(TOKEN, "");
    }

    @Override
    public void setToken(String token) {
        sharedPreferences.edit().putString(TOKEN, token).commit();
    }

    @Override
    public int getLoginType() {
        return sharedPreferences.getInt(LOGIN_TYPE, 0);
    }

    @Override
    public void setLoginType(int loginType) {
        sharedPreferences.edit().putInt(LOGIN_TYPE, loginType).commit();
    }

    @Override
    public int getSelectedRestaurantId() {
        return sharedPreferences.getInt(SELECTED_RESTAURANT_ID, 0);
    }

    @Override
    public void setSelectedRestaurantId(int restaurantId) {
        sharedPreferences.edit().putInt(SELECTED_RESTAURANT_ID, restaurantId).commit();
    }

    @Override
    public boolean isLoyaltyEnable() {
        return sharedPreferences.getBoolean(LOYALTY_PROGRAM_ENABLE, false);
    }

    @Override
    public void enableLoyalty(boolean enable) {
        sharedPreferences.edit().putBoolean(LOYALTY_PROGRAM_ENABLE, enable).commit();
    }

    @Override
    public void setUserHasAvatar(boolean hasAvatar) {
        sharedPreferences.edit().putBoolean(USER_HAS_AVATAR, hasAvatar).commit();
    }

    @Override
    public boolean hasUserAvatar() {
        return sharedPreferences.getBoolean(USER_HAS_AVATAR, false);
    }

    @Override
    public List<DishCategory> getSelectedRestaurantCategory() {
        Gson gson = new Gson();
        String categoriesJson = sharedPreferences.getString(SELECTED_RESTAURANT_CATEGORY, "");
        Type type = new TypeToken<List<DishCategory>>(){}.getType();
        return gson.fromJson(categoriesJson, type);
    }

    @Override
    public void setSelectedRestaurantCategory(List<DishCategory> dishCategoryList) {
        Gson gson = new Gson();
        sharedPreferences.edit().putString(SELECTED_RESTAURANT_CATEGORY, gson.toJson(dishCategoryList)).commit();
    }

    @Override
    public String getFCMToken() {
        return sharedPreferences.getString(FCM_TOKEN, "");
    }

    @Override
    public void setFCMToken(String fcmToken) {
        sharedPreferences.edit().putString(FCM_TOKEN, fcmToken).commit();
    }

    public static void setLoginDataInPreferences(String token, UserData userData, int loginType) {
        CorePreferencesImpl corePref = CorePreferencesImpl.getCorePreferences();
        corePref.setFirstStart(false);
        corePref.setLoginType(loginType);
        corePref.setToken(token);
//        corePref.setAccessToken(token.getAccessToken());
//        corePref.setRefreshToken(token.getRefreshToken());
//        corePref.setExpiresToken(token.getExpiresIn());
        corePref.setUserData(userData);
    }

}
