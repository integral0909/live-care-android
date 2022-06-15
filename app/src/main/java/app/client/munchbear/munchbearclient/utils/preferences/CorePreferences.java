package app.client.munchbear.munchbearclient.utils.preferences;

import java.util.List;

import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.dish.DishCategory;
import app.client.munchbear.munchbearclient.model.menu.RestaurantMenu;
import app.client.munchbear.munchbearclient.model.user.UserData;

/**
 * Created by Roman on 7/5/2018.
 */
public interface CorePreferences {

    boolean firstStart();

    void setFirstStart(boolean firstStart);

    boolean isNeedForceOpenMenu();

    void setForceOpenMenu(boolean isNeed);

    boolean isNeedForceOpenMyOrder();

    void setForceOpenMyOrder(boolean isNeed);

    boolean isNeedOpenReservationTab();

    void setNeedOpenReservationTab(boolean isNeed);

    UserData getUserData();

    void setUserData(UserData userDataData);

    Address getDefaultDeliveryAddress();

    void setDefaultDeliveryAddress(Address address);

    List<Address> getPreviousDeliveryAddressList();

    void setPreviousDeliveryAddressList(List<Address> addressList);

    String getAccessToken();

    void setAccessToken(String accessToken);

    String getRefreshToken();

    void setRefreshToken(String refreshToken);

    String getToken();

    void setToken(String token);

    long getExpiresToken();

    void setExpiresToken(long expiresToken);

    int getLoginType();

    void setLoginType(int loginType);

    int getSelectedRestaurantId();

    void setSelectedRestaurantId(int restaurantId);

    boolean isLoyaltyEnable();

    void enableLoyalty(boolean enable);

    List<DishCategory> getSelectedRestaurantCategory();

    void setSelectedRestaurantCategory(List<DishCategory> dishCategoryList);

    String getFCMToken();

    void setFCMToken(String fcmToken);

    void setUserHasAvatar(boolean hasAvatar);

    boolean hasUserAvatar();
}
