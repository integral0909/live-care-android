package app.client.munchbear.munchbearclient.utils;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.order.make.MakeOrder;
import app.client.munchbear.munchbearclient.model.order.OrderLocation;
import app.client.munchbear.munchbearclient.model.order.OrderUserContact;

import static app.client.munchbear.munchbearclient.utils.OrderRequestBuilder.EAT_IN;

/**
 * Created by Roman on 7/7/2018.
 */
public class ValidationsUtils {

    public static Pattern emailPatter = Patterns.EMAIL_ADDRESS;
    public static Pattern phonePatter = Patterns.PHONE;

    public static boolean isTextLengthValid(TextInputLayout et, String password, String emptyText, String notValid, int lenght) {
        if (TextUtils.isEmpty(password)) {
            et.setError(emptyText);
            return false;
        } else if (password.length() < lenght){
            et.setError(notValid);
            return false;
        }
            return true;
    }

    public static boolean isValidPattern(TextInputLayout et, String email, Pattern patern,  String emptyText, String notValid) {
        Matcher matcher = patern.matcher(email);
        if (TextUtils.isEmpty(email)) {
            et.setError(emptyText);
            return false;
        } else if (!matcher.matches()){
            et.setError(notValid);
            return false;
        }
        return true;
    }

    public static boolean isRepeatPasswordValid(TextInputLayout et, String password, String repeatPassword, String emptyText, String notValid) {
        if (TextUtils.isEmpty(repeatPassword)) {
            et.setError(emptyText);
            return false;
        } else if (!password.equals(repeatPassword)){
            et.setError(notValid);
            return false;
        }
        return true;
    }

    public static boolean isEmpty(TextInputLayout et, String text, String emptyText) {
        if (TextUtils.isEmpty(text)) {
            et.setError(emptyText);
            return false;
        } else {
            return true;
        }
    }

    public static boolean isMakeOrderDataValid(MakeOrder makeOrder, Context context) {
        return isUserContactValid(makeOrder.getUserContact(), context)
                && isPaymentValid(makeOrder.getPaymentType(), context)
                && isTableNumberValid(makeOrder.getOrderDelivery().getType(), makeOrder.getOrderDelivery().getOrderLocation(), context);
    }

    public static boolean isPaymentValid(String paymentType, Context context) {
        //TODO Add validation for payment card
        if (paymentType.length() > 0) {
            return true;
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.toast_not_valid_payment_type), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static boolean isUserContactValid(OrderUserContact orderUserContact, Context context) {
        if (orderUserContact != null && !TextUtils.isEmpty(orderUserContact.getUserName()) && !TextUtils.isEmpty(orderUserContact.getUserPhone())) {
            return true;
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.toast_not_valid_contact_information), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static boolean isTableNumberValid(String deliveryType, OrderLocation orderLocation, Context context) {
        if (deliveryType.equals(EAT_IN)) {
            Integer tableNumber = orderLocation.getTableNumber();
            if (tableNumber != null && tableNumber != 0) {
                return true;
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.toast_not_valid_table_number), Toast.LENGTH_LONG).show();
                return false;
            }
        } else {
            return true;
        }
    }

}
