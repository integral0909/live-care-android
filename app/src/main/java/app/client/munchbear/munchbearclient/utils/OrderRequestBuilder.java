package app.client.munchbear.munchbearclient.utils;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.model.modifier.ExcludeModifier;
import app.client.munchbear.munchbearclient.model.modifier.MandatoryModifier;
import app.client.munchbear.munchbearclient.model.modifier.OptionalModifier;
import app.client.munchbear.munchbearclient.model.order.Cart;
import app.client.munchbear.munchbearclient.model.order.make.MakeOrder;
import app.client.munchbear.munchbearclient.model.order.make.MakeOrderItems;
import app.client.munchbear.munchbearclient.model.order.OrderDelivery;
import app.client.munchbear.munchbearclient.model.order.OrderLocation;
import app.client.munchbear.munchbearclient.model.order.OrderModifiers;
import app.client.munchbear.munchbearclient.model.order.OrderUserContact;
import app.client.munchbear.munchbearclient.model.order.ordermodifier.MakeOrderExcludeModifier;
import app.client.munchbear.munchbearclient.model.order.ordermodifier.MakeOrderMandatoryModifier;
import app.client.munchbear.munchbearclient.model.order.ordermodifier.MakeOrderOptionalModifier;
import app.client.munchbear.munchbearclient.model.user.UserData;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;

/**
 * @author Roman H.
 */

public class OrderRequestBuilder {

    public static final String EAT_IN = "eat-in";
    public static final String DELIVERY = "delivery";
    public static final String PICK_UP = "pick-up";

    private static OrderRequestBuilder orderBuilderInstance;

    private static MakeOrder makeOrder;
    private static int selectedDeliveryType;

    private OrderRequestBuilder() {

    }

    public static OrderRequestBuilder getInstance() {
        if (orderBuilderInstance == null) {
            orderBuilderInstance = new OrderRequestBuilder();
        }

        return orderBuilderInstance;
    }

    public MakeOrder initOrderBuilder() {
        makeOrder = new MakeOrder();
        selectedDeliveryType = Cart.getInstance().getSelectedDeliveryType();
        firstInitUserContact();
        setOrderItems();
        setDeliveryTime("");
        setPaymentType("");
        setComment("");
        return makeOrder;
    }

    public MakeOrder getMakeOrder() {
        return makeOrder;
    }

    private void firstInitUserContact() {
        UserData userData = CorePreferencesImpl.getCorePreferences().getUserData();
        setUserContact(userData.getName(), userData.getPhone());
    }

    public void setUserContact(String name, String phone) {
        makeOrder.setUserContact(new OrderUserContact(name, phone));
    }

    public void setDeliveryTime(String time) {
        //TODO If only delivery type DELIVERY must has specific time - uncomment next line
//        OrderDelivery orderDelivery = new OrderDelivery(DeliveryVariant.convertDeliveryTypeIntToString(selectedDeliveryType), selectedDeliveryType == DeliveryVariant.TYPE_DELIVERY ? time : "");
        //TODO and delete this line
        OrderDelivery orderDelivery = new OrderDelivery(DeliveryVariant.convertDeliveryTypeIntToString(selectedDeliveryType), time);
        makeOrder.setOrderDelivery(orderDelivery);
    }

    public void setComment(String comment) {
        makeOrder.setComment(comment);
    }

    public void setPaymentType(String paymentType) {
        makeOrder.setPaymentType(paymentType);
    }

    public void setOrderLocation(int tableNumber) {
        OrderLocation orderLocation = new OrderLocation(tableNumber);
        setupOrderLocation(orderLocation);
    }

    public void setOrderLocation(Address address) {
        OrderLocation orderLocation = new OrderLocation(address.getAddressForCourier(), address.getUnitNumber(), address.getLat(), address.getLng());
        setupOrderLocation(orderLocation);
    }

    private void setupOrderLocation(OrderLocation orderLocation) {
        if (makeOrder.getOrderDelivery() != null) {
            makeOrder.getOrderDelivery().setOrderLocation(orderLocation);
        }
    }

    private void setOrderItems() {
        List<MakeOrderItems> makeOrderItemsList = new ArrayList<>();
        List<Dish> dishList = Cart.getInstance().getDishList();

        for (Dish dish : dishList) {
            int dishSizeId = dish.getSelectedDishSize().getId();
            int dishQuantity  = dish.getCount();

            MakeOrderItems makeOrderItems = new MakeOrderItems(dishSizeId, dishQuantity, getOrderItemModifiers(dish));
            makeOrderItemsList.add(makeOrderItems);
        }

        makeOrder.setMakeOrderItemsList(makeOrderItemsList);
    }

    private OrderModifiers getOrderItemModifiers(Dish dish) {
        return new OrderModifiers(getMakeExcludeList(dish), getMakeOptionalList(dish), getMakeMandatoryList(dish));
    }

    private List<MakeOrderExcludeModifier> getMakeExcludeList(Dish dish) {
        List<MakeOrderExcludeModifier> makeOrderExcludeModifierList = new ArrayList<>();

        for (ExcludeModifier excludeModifier : DishHelper.getSelectedExclude(dish.getExcludeModifierList())) {
            makeOrderExcludeModifierList.add(new MakeOrderExcludeModifier(excludeModifier.getId()));
        }

        return makeOrderExcludeModifierList;
    }

    private List<MakeOrderOptionalModifier> getMakeOptionalList(Dish dish) {
        List<MakeOrderOptionalModifier> makeOrderOptionalModifierList= new ArrayList<>();

        for (OptionalModifier optionalModifier : DishHelper.getAllSelectedOptional(dish.getOptionalModifierList())) {
            makeOrderOptionalModifierList.add(new MakeOrderOptionalModifier(optionalModifier.getId(), optionalModifier.getCounter()));
        }

        return makeOrderOptionalModifierList;
    }

    private List<MakeOrderMandatoryModifier> getMakeMandatoryList(Dish dish) {
        List<MakeOrderMandatoryModifier> makeOrderMandatoryModifierList = new ArrayList<>();

        for (MandatoryModifier mandatoryModifier : DishHelper.getAllSelectedMandatory(dish.getMandatoryModifierList())) {
            makeOrderMandatoryModifierList.add(new MakeOrderMandatoryModifier(mandatoryModifier.getId()));
        }

        return makeOrderMandatoryModifierList;
    }


}
