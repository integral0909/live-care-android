package app.client.munchbear.munchbearclient.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import app.client.munchbear.munchbearclient.R;

public class DeliveryVariant {

    //Delivery status
    public static final int ORDER_STATUS_CREATED = 0;
    public static final int ORDER_STATUS_PENDING = 1;
    public static final int ORDER_STATUS_ACCEPTED = 2;
    public static final int ORDER_STATUS_SUCCESS = 3;
    public static final int ORDER_STATUS_DECLINE = 4;
    public static final int ORDER_STATUS_IN_DELIVERY = 5;
    public static final int ORDER_STATUS_FAILED = 6;
    public static final int ORDER_STATUS_CANCELED = 7;
    public static final int ORDER_STATUS_COOKING = 8;
    public static final int ORDER_STATUS_NEW = 666;

    //Delivery type
    public static final int TYPE_DELIVERY = 10;
    public static final int TYPE_PICK_UP = 11;
    public static final int TYPE_EAT_IN = 12;
    public static final int TYPE_TABLE_RESERVATION = 13;

    //Reservation status
    public static final int RES_STATUS_PENDING = 10;
    public static final int RES_STATUS_ACCEPTED = 20;
    public static final int RES_STATUS_CANCELED_USER = 31;
    public static final int RES_STATUS_CANCELED_ADMIN_PENDING = 32;
    public static final int RES_STATUS_CANCELED_ADMIN_ACCEPTED = 33;

    public static final String EAT_IN = "eat-in";
    public static final String DELIVERY = "delivery";
    public static final String PICK_UP = "pick-up";

    public static String getStatusToString(int type, int status, Context context) {
        return type == TYPE_TABLE_RESERVATION ? getReservationStatusToString(status, context)
                : getOrderStatusToString(status, context);
    }

    public static String getOrderStatusToString(int status, Context context) {
        Resources resources = context.getResources();
        switch (status) {
            case ORDER_STATUS_CREATED: return resources.getString(R.string.del_status_pending);
            case ORDER_STATUS_ACCEPTED: return resources.getString(R.string.del_status_accepted);
            case ORDER_STATUS_PENDING: return resources.getString(R.string.del_status_pending);
            case ORDER_STATUS_SUCCESS: return resources.getString(R.string.del_status_success);
            case ORDER_STATUS_DECLINE : return resources.getString(R.string.del_status_decline);
            case ORDER_STATUS_IN_DELIVERY : return resources.getString(R.string.del_status_in_delivery);
            case ORDER_STATUS_FAILED : return resources.getString(R.string.del_status_in_failed);
            case ORDER_STATUS_CANCELED : return resources.getString(R.string.del_status_canceled);
            case ORDER_STATUS_COOKING : return resources.getString(R.string.del_status_cooking);
            default: return "Wrong status";
        }
    }

    public static String getReservationStatusToString(int status, Context context) {
        Resources resources = context.getResources();
        switch (status) {
            case RES_STATUS_PENDING : return resources.getString(R.string.res_status_pending);
            case RES_STATUS_ACCEPTED : return resources.getString(R.string.res_status_accepted);
            case RES_STATUS_CANCELED_USER : return resources.getString(R.string.res_status_canceled_admin);
            case RES_STATUS_CANCELED_ADMIN_PENDING : return resources.getString(R.string.res_status_canceled_user);
            case RES_STATUS_CANCELED_ADMIN_ACCEPTED : return resources.getString(R.string.res_status_canceled_user);
            default: return "Wrong status";
        }
    }

    public static String getTypeToString(int type, Context context) {
        Resources resources = context.getResources();
        switch (type) {
            case TYPE_DELIVERY : return resources.getString(R.string.del_type_delivery).toUpperCase();
            case TYPE_PICK_UP : return resources.getString(R.string.del_type_pick_up).toUpperCase();
            case TYPE_EAT_IN : return resources.getString(R.string.del_type_eat_in).toUpperCase();
            case TYPE_TABLE_RESERVATION : return resources.getString(R.string.del_type_table_reservation).toUpperCase();
            default: return "Wrong type";
        }
    }

    public static String getDeliveryFromType(int type, Context context) {
        Resources resources = context.getResources();
        switch (type) {
            case TYPE_DELIVERY : return resources.getString(R.string.del_type_delivery_from).toUpperCase();
            case TYPE_PICK_UP : return resources.getString(R.string.del_type_pick_up_at).toUpperCase();
            case TYPE_EAT_IN : return resources.getString(R.string.del_type_eat_in).toUpperCase();
            default: return "Wrong type";
        }
    }

    public static Drawable getStatusDrawable(int status, boolean isReservation, Context context) {
        if (isReservation) {
            if (status == RES_STATUS_PENDING) {
                return context.getResources().getDrawable(R.drawable.corner_btn_green_order_status);
            } else {
                return context.getResources().getDrawable(R.drawable.corner_btn_grey_order_status);
            }
        } else {
            if (status == ORDER_STATUS_ACCEPTED || status == ORDER_STATUS_IN_DELIVERY
                    || status == ORDER_STATUS_PENDING || status == ORDER_STATUS_COOKING || status == ORDER_STATUS_CREATED) {
                return context.getResources().getDrawable(R.drawable.corner_btn_green_order_status);
            } else {
                return context.getResources().getDrawable(R.drawable.corner_btn_grey_order_status);
            }
        }
    }

    public static int getOrderStatusColor(int status, Context context) {
        if (status == ORDER_STATUS_DECLINE || status == ORDER_STATUS_FAILED) {
            return context.getResources().getColor(R.color.darkGreyProfile);
        } else if (status == ORDER_STATUS_ACCEPTED || status == ORDER_STATUS_IN_DELIVERY
                || status == ORDER_STATUS_PENDING || status == ORDER_STATUS_COOKING || status == ORDER_STATUS_CREATED) {
            return context.getResources().getColor(R.color.bottomMenuTextGreen);
        } else {
            return context.getResources().getColor(R.color.bottomMenuDarkGrey);
        }
    }

    public static int getReservationStatusColor(int status, Context context) {
        if (status == RES_STATUS_ACCEPTED) {
            return context.getResources().getColor(R.color.bottomMenuTextGreen);
        } else {
            return context.getResources().getColor(R.color.bottomMenuDarkGrey);
        }
    }

    public static String convertDeliveryTypeIntToString(int selectedDeliveryType) {
        switch (selectedDeliveryType) {
            case TYPE_DELIVERY: return DELIVERY;
            case TYPE_EAT_IN: return EAT_IN;
            case TYPE_PICK_UP: return PICK_UP;
            default: return "";
        }
    }

    public static int convertDeliveryTypeStringToInt(String deliveryType) {
        switch (deliveryType) {
            case DELIVERY: return TYPE_DELIVERY;
            case EAT_IN: return TYPE_EAT_IN;
            case PICK_UP: return TYPE_PICK_UP;
            default: return 0;
        }
    }

}
