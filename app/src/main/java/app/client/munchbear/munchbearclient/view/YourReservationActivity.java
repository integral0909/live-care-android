package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Calendar;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.Address;
import app.client.munchbear.munchbearclient.model.reservation.CreatedReservation;
import app.client.munchbear.munchbearclient.model.restaurant.Restaurant;
import app.client.munchbear.munchbearclient.model.user.UserAvatar;
import app.client.munchbear.munchbearclient.model.user.UserData;
import app.client.munchbear.munchbearclient.model.request.CreateReservationRequestModel;
import app.client.munchbear.munchbearclient.utils.DateUtils;
import app.client.munchbear.munchbearclient.utils.DeliveryVariant;
import app.client.munchbear.munchbearclient.utils.LoginType;
import app.client.munchbear.munchbearclient.utils.MunchBearDatePickerDialog;
import app.client.munchbear.munchbearclient.utils.MunchBearTimePickerDialog;
import app.client.munchbear.munchbearclient.utils.preferences.CorePreferencesImpl;
import app.client.munchbear.munchbearclient.viewmodel.YourReservationActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.client.munchbear.munchbearclient.view.ChangeDataActivity.CHANGE_AMOUNT_OF_PEOPLE;
import static app.client.munchbear.munchbearclient.view.ChangeDataActivity.CHANGE_CHILD_CHAIR;
import static app.client.munchbear.munchbearclient.view.ChangeDataActivity.CHANGE_COMMENT;

public class YourReservationActivity extends YourOrderBaseActivity {

    @BindView(R.id.customerAvatar) RoundedImageView customerAvatar;
    @BindView(R.id.contactInfoCard) RelativeLayout contactInfoCard;
    @BindView(R.id.orderContactInfoLayout) LinearLayout orderContactInfoLayout;
    @BindView(R.id.contactInfoTitle) TextView contactInfoTitle;
    @BindView(R.id.customerPhone) TextView customerPhone;
    @BindView(R.id.customerName) TextView customerName;
    @BindView(R.id.placedTime) TextView placedTime;

    @BindView(R.id.deliveryStatusTxt) TextView deliveryStatusTxt;
    @BindView(R.id.deliveryToTitle) TextView deliveryToTitle;
    @BindView(R.id.deliveryMap) ImageView deliveryMap;
    @BindView(R.id.asSoonLayout) LinearLayout asSoonLayout;
    @BindView(R.id.orderPlacedTitle) TextView orderPlacedTitle;
    @BindView(R.id.orderNumber) TextView orderNumber;
    @BindView(R.id.noRefundTxt) TextView noRefundTxt;
    @BindView(R.id.yourOrderNumberTitle) TextView yourOrderNumberTitle;
    @BindView(R.id.headerStatus) LinearLayout headerStatus;
    @BindView(R.id.deliveryTabMainLayout) LinearLayout deliveryTabMainLayout;
    @BindView(R.id.deliveryTabDivideLayout) View deliveryTabDivideLayout;
    @BindView(R.id.mapMarker) ImageView mapMarker;

    @BindView(R.id.arrowChangeAddress) ImageView arrowChangeAddress;
    @BindView(R.id.changeCountPeople) TextView changeCountPeople;
    @BindView(R.id.changeChildChairTxt) TextView changeChildChairTxt;

    @BindView(R.id.commentTxt) TextView commentTxt;
    @BindView(R.id.commentTitle) TextView commentTitle;
    @BindView(R.id.orderCommentLayout) LinearLayout orderCommentLayout;

    @BindView(R.id.childChairCount) TextView childChairCount;
    @BindView(R.id.childChairText) TextView childChairText;
    @BindView(R.id.childChairTitle) TextView childChairTitle;

    @BindView(R.id.amount_people) TextView amount_people;
    @BindView(R.id.amountText) TextView amountText;
    @BindView(R.id.amountOfPeopleTitle) TextView amountOfPeopleTitle;
    @BindView(R.id.divide2) View divide2;
    @BindView(R.id.divide1) View divide1;
    @BindView(R.id.actionBtnTxt) Button actionBtnTxt;
    @BindView(R.id.actionResBtn) LinearLayout actionResBtn;

    @BindView(R.id.emptyAddress) TextView emptyAddress;
    @BindView(R.id.addressFields) LinearLayout addressFields;
    @BindView(R.id.postIndex) TextView postIndex;
    @BindView(R.id.street) TextView street;
    @BindView(R.id.cityAndCountry) TextView cityAndCountry;

    @BindView(R.id.emptyContact) TextView emptyContact;

    @BindView(R.id.whenDate) TextView whenDate;
    @BindView(R.id.whenTime) TextView whenTime;

    private YourReservationActivityViewModel reservationActivityViewModel;
    private int type;
    private int status;
    private boolean isNewReservation;
    private boolean applyChangesInList = false;
    private boolean creatingReservation = false;
    private Calendar reservationCalendar;
    private Restaurant restaurant;
    private Address restaurantAddress;
    private CreatedReservation createdReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_reservation);
        ButterKnife.bind(this);

        reservationActivityViewModel = ViewModelProviders.of(this).get(YourReservationActivityViewModel.class);
        reservationCalendar = Calendar.getInstance();
        restaurant = new Restaurant(0);
        type = DeliveryVariant.TYPE_TABLE_RESERVATION;

        initObservers();
        getDataFromIntent();
        initViews();

    }

    private void initObservers() {
        reservationActivityViewModel.getCreatedReservationLiveData().observe(this, reservation -> {
            CorePreferencesImpl.getCorePreferences().setNeedOpenReservationTab(true);
            openMainActivityClearTop(false);
        });

        reservationActivityViewModel.getCreateReservationErrorLiveData().observe(this, error -> {
            if (error) {
                Toast.makeText(this, getResources().getString(R.string.toast_create_reservation_error), Toast.LENGTH_SHORT).show();
            } else {
                creatingReservation = false;
            }
        });

        reservationActivityViewModel.getNotValidFields().observe(this, notValidString -> {
            Toast.makeText(this, getResources().getString(notValidString), Toast.LENGTH_SHORT).show();
        });

        reservationActivityViewModel.getCancelReservationErrorLiveData().observe(this, error -> {
            Toast.makeText(this, getResources().getString(R.string.toast_res_canceling_error), Toast.LENGTH_SHORT).show();
        });

        reservationActivityViewModel.getCancelReservationLivaData().observe(this, cancelReservation -> {
            if (cancelReservation) {
                createdReservation.setStatus(DeliveryVariant.RES_STATUS_CANCELED_USER);
                applyChangesInList = true;
                onBackPressed();
            }
        });

        reservationActivityViewModel.getUpdateReservationLiveData().observe(this, updatedReservation -> {
            applyChangesInList = true;
            setChangedContactData(customerName, customerPhone, updatedReservation.getName(), updatedReservation.getPhone());
            updateReservationModel(updatedReservation.getName(), updatedReservation.getPhone());
            showContactView(false);
            Toast.makeText(this, getResources().getString(R.string.toast_res_user_data_updated), Toast.LENGTH_SHORT).show();
        });

        reservationActivityViewModel.getUpdateReservationErrorLiveData().observe(this, error -> {
            Toast.makeText(this, getResources().getString(R.string.toast_res_user_data_updated_error), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateReservationModel(String name, String phone) {
        createdReservation.setName(name);
        createdReservation.setPhone(phone);
    }

    private void getDataFromIntent() {
        if (getIntent().hasExtra(CreatedReservation.KEY_CREATED_RESERVATION)) {
            createdReservation = getIntent().getParcelableExtra(CreatedReservation.KEY_CREATED_RESERVATION);
            if (createdReservation != null) {
                restaurant = createdReservation.getRestaurant();
                restaurantAddress = restaurant.getAddress();
            }
            status = createdReservation.getStatus();
            isNewReservation = false;
        } else {
            status = DeliveryVariant.ORDER_STATUS_NEW;
            isNewReservation = true;
        }
    }

    private void initViews() {
        initHeaderViews();
        preSetupContactViews();
        initChangeContactButton(contactInfoTitle, orderContactInfoLayout, getResources().getString(R.string.your_res_contact_title),
                customerName, customerPhone, status);
        setupMap();
        initDeliveryAddress(restaurantAddress, arrowChangeAddress, addressFields, emptyAddress, street, postIndex, cityAndCountry);
        initDeliveryTabView(deliveryTabMainLayout, deliveryTabDivideLayout, type);
        initViewsHeaders();
        initCommentViews();
        initChangeTxtViews();
        initWhenViews(isNewReservation, whenDate, whenTime, isNewReservation ? 0 : createdReservation.getWhen());
        initPeopleCountAndComment();
    }

    private void preSetupContactViews() {
        UserData userData = isNewReservation ? CorePreferencesImpl.getCorePreferences().getUserData()
                : new UserData(createdReservation.getName(), createdReservation.getPhone());

        initContactInfoViews(customerName, customerPhone, customerAvatar, emptyContact, contactInfoCard,
                true, userData);
    }

    private void setupMap() {
        if (restaurant.getAddress() != null && restaurant.getAddress() != null) {
            LatLng latLng = new LatLng(restaurant.getAddress().getLat(), restaurant.getAddress().getLng());
            initMapViews(latLng, deliveryMap, mapMarker, asSoonLayout, divide2, deliveryToTitle, type);
        } else {
            initMapViews(null, deliveryMap, mapMarker, asSoonLayout, divide2, deliveryToTitle, type);
        }
    }

    private void initChangeTxtViews() {
        changeCountPeople.setVisibility(isNewReservation ? View.VISIBLE : View.GONE);
        changeChildChairTxt.setVisibility(isNewReservation ? View.VISIBLE : View.GONE);
    }

    private void initPeopleCountAndComment() {
        if (!isNewReservation) {
            amount_people.setText(String.valueOf(createdReservation.getAmountOfPeople()));
            childChairCount.setText(String.valueOf(createdReservation.getChildChairs()));
            if (createdReservation.getComment() != null) {
                commentTxt.setText(createdReservation.getComment());
                commentTxt.setTextColor(getResources().getColor(R.color.darkGrey));
            } else {
                orderCommentLayout.setVisibility(View.GONE);
            }
        }
    }

    private void initHeaderViews() {
        if (!isNewReservation) {
            yourOrderNumberTitle.setText(R.string.your_res_res_title);
            orderPlacedTitle.setText(R.string.your_res_res_placed_title);
            placedTime.setText(DateUtils.getDateMMMMddyyyyhhmma(createdReservation.getWhen()));
            divide1.setVisibility(View.GONE);
            actionResBtn.setVisibility(status == DeliveryVariant.RES_STATUS_ACCEPTED
                    || status == DeliveryVariant.RES_STATUS_PENDING ? View.VISIBLE : View.GONE);
            setupStatusHeader(createdReservation.getStatus(), createdReservation.getId(), orderNumber, deliveryStatusTxt, noRefundTxt, true);
        } else {
            headerStatus.setVisibility(View.GONE);
            emptyAddress.setText(getResources().getString(R.string.your_res_select_restaurant));
            actionBtnTxt.setText(getResources().getString(R.string.your_res_place_registration));
            actionBtnTxt.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

    }

    private void initViewsHeaders() {
        setDataToHeaderViews(status, amountOfPeopleTitle, amountText, amount_people);
        setDataToHeaderViews(status, childChairTitle, childChairText, childChairCount);
    }

    private void initCommentViews() {
        commentTitle.setVisibility(View.GONE);
        commentTxt.setText(getResources().getString(R.string.your_orders_comment));
        commentTxt.setTextColor(isNewReservation ? getResources().getColor(R.color.bottomMenuDarkGrey)
                : getResources().getColor(R.color.darkGrey));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ChangeDataActivity.CHANGE_DATA_REQUEST_CODE:
                    int changeType = data.getIntExtra(ChangeDataActivity.CHANGE_TYPE, 1);
                    setChangedData(changeType, data.getStringExtra(ChangeDataActivity.RESULT_VALUE));
                    break;
                case ChangeProfileActivity.CHANGE_PROFILE_REQUEST_CODE:
                    updateCustomerData(data);
                    break;
                case SelectLocationActivity.SELECT_LOCATION_REQUEST_CODE:
                    restaurant = data.getParcelableExtra(Restaurant.RESTAURANT);
                    if (restaurant != null) {
                        restaurantAddress = restaurant.getAddress();
                        initDeliveryAddress(restaurantAddress, arrowChangeAddress, addressFields, emptyAddress, street, postIndex, cityAndCountry);
                        setupMap();
                    }
                    break;
            }
        }
    }

    private void updateCustomerData(Intent data) {
        String name = data.getStringExtra(UserData.KEY_NAME);
        String phone = data.getStringExtra(UserData.KEY_PHONE);

        if (!isNewReservation) {
            reservationActivityViewModel.updateReservation(createdReservation.getId(), name, phone);
        } else {
            setChangedContactData(customerName, customerPhone, name, phone);
            showContactView(false);
        }
    }

    private void showContactView(boolean isEmpty) {
        if (CorePreferencesImpl.getCorePreferences().getLoginType() == LoginType.GUEST) {
            customerAvatar.setVisibility(View.GONE);
        } else {
            customerAvatar.setImageBitmap(UserAvatar.getUserAvatar(this));
        }
        emptyContact.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        contactInfoCard.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void setChangedData(int changeType, String value) {
        switch (changeType) {
            case CHANGE_AMOUNT_OF_PEOPLE:
                amount_people.setText(value);
                break;
            case CHANGE_CHILD_CHAIR:
                childChairCount.setText(value);
                break;
            case CHANGE_COMMENT:
                commentTxt.setText(value);
                commentTxt.setTextColor(getResources().getColor(R.color.darkGrey));
                break;
        }
    }

    private CreateReservationRequestModel buildRequestModel() {
        String when = DateUtils.getReservationFormat(reservationCalendar);
        String name = customerName.getText().toString();
        String phone = customerPhone.getText().toString();
        int childChairs = Integer.parseInt(childChairCount.getText().toString());
        int amountOfPeople = Integer.parseInt(amount_people.getText().toString());
        String comment = commentTxt.getText().toString();

        return new CreateReservationRequestModel(when,
                name, phone, childChairs, amountOfPeople, isCommentEmpty(comment) ? null : comment);
    }

    private boolean isCommentEmpty(String comment) {
        return comment.equals(getResources().getString(R.string.your_orders_comment)) || comment.length() == 0;
    }

    private boolean isDateSelected() {
        return !whenDate.getText().toString().equals(getResources().getString(R.string.your_res_empty_date))
                && !whenTime.getText().toString().equals(getResources().getString(R.string.your_res_empty_time));
    }

    @Override
    public void onBackPressed() {
        if (applyChangesInList) {
            Intent intent = new Intent();
            intent.putExtra(CreatedReservation.KEY_CREATED_RESERVATION, createdReservation);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        onBackPressed();
    }

    @OnClick(R.id.changeAmountPeople)
    public void clickChangePeopleCount() {
        if (isNewReservation) {
            openChangeDataActivity(ChangeDataActivity.CHANGE_AMOUNT_OF_PEOPLE);
        }
    }

    @OnClick(R.id.changeChildChair)
    public void clickChangeChairCount() {
        if (isNewReservation) {
            openChangeDataActivity(ChangeDataActivity.CHANGE_CHILD_CHAIR);
        }
    }

    @OnClick(R.id.actionResBtn)
    public void cancelReservation() {
        if (isNewReservation) {
            if(!creatingReservation) {
                creatingReservation = true;
                reservationActivityViewModel.handleCreateReservation(restaurant.getId(), isDateSelected(),
                        buildRequestModel());
            }
        } else {
            reservationActivityViewModel.cancelReservation(createdReservation.getId());
        }
    }

    @OnClick(R.id.addressCard)
    public void changeAddressFragment() {
        if (isNewReservation) {
            openChangeAddressFragment(status, type);
        } else {
            openMapDetails(restaurantAddress, type);
        }
    }

    @OnClick(R.id.orderCommentLayout)
    public void clickComment() {
        if (isNewReservation) {
            openChangeDataActivity(ChangeDataActivity.CHANGE_COMMENT);
        }
    }

    @OnClick(R.id.whenDate)
    public void clickWhenDate() {
        if (isNewReservation) {
            MunchBearDatePickerDialog datePickerDialog = new MunchBearDatePickerDialog();
            datePickerDialog.setOnDateSetListener((datePicker, year, month, dayOfMonth) -> {
                reservationCalendar.set(year, month, dayOfMonth);
                whenDate.setText(DateUtils.getDateMMMMDYYYY(year, month, dayOfMonth));
            });
            datePickerDialog.show(getSupportFragmentManager(), "date picker");
        }
    }

    @OnClick(R.id.whenTime)
    public void clickWhenTime() {
        if (isNewReservation) {
            MunchBearTimePickerDialog timePickerDialog = new MunchBearTimePickerDialog();
            timePickerDialog.setOnTimeSetListener((timePicker, hourOfDay, minute) -> {
                reservationCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                reservationCalendar.set(Calendar.MINUTE, minute);
                whenTime.setText(DateUtils.getTimeHHmma(hourOfDay, minute));
            });
            timePickerDialog.show(getSupportFragmentManager(), "time picker");
        }
    }

}
