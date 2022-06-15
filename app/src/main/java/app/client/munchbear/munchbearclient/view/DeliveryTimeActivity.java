package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.utils.DateUtils;
import app.client.munchbear.munchbearclient.viewmodel.DeliveryTimeActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeliveryTimeActivity extends BaseActivity {

    @BindView(R.id.dateTxt) TextView dateTxt;
    @BindView(R.id.timeTxt) TextView timeTxt;
    @BindView(R.id.calendar) CalendarView calendar;
    @BindView(R.id.timePicker) TimePicker timePicker;
    @BindView(R.id.asSoonBtnIcon) ImageView asSoonBtnIcon;
    @BindView(R.id.specifTimeBtnIcon) ImageView specifTimeBtnIcon;
    @BindView(R.id.calendarLayour) LinearLayout calendarLayout;

    public static final int DELIVERY_TIME_REQUEST_CODE = 105;

    public static final String KEY_DELIVERY_TIME_TYPE = "key.delivery.time.type";
    public static final String KEY_DELIVERY_TIME_DATE = "key.delivery.time.date";
    public static final String KEY_DELIVERY_TIME_TIME = "key.delivery.time.time";
    public static final String KEY_DELIVERY_TIME_TIMESTAMP = "key.delivery.time.timestamp";

    public static final int TYPE_NONE_DELIVERY_TYPE = 0;
    public static final int TYPE_AS_SOON = 1;
    public static final int TYPE_SPECIFIC_TIME = 2;

    private DeliveryTimeActivityViewModel deliveryTimeActivityViewModel;
    private int deliveryTimeType = 0;
    private long selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_time);
        ButterKnife.bind(this);

        deliveryTimeActivityViewModel = ViewModelProviders.of(this).get(DeliveryTimeActivityViewModel.class);
        deliveryTimeType = TYPE_AS_SOON;

        initObservers();

        initCalendar();
        initTimePicker();
        setupStarterTimeAndDate();

    }

    private void initCalendar() {
        calendar.setMinDate(System.currentTimeMillis() - 1000);
        calendar.setOnDateChangeListener((calendarView, year, month, day) -> dateTxt.setText(DateUtils.getDateMMMMDYYYY(year, month, day)));
    }

    private void initTimePicker() {
        timePicker.setOnTimeChangedListener((timePicker, hours, minute) -> handleSelectedTime(hours, minute));
    }

    private void handleSelectedTime(int hours, int minute) {
        selectedTime = DateUtils.getTimeHHmm(hours, minute);
        timeTxt.setText(DateUtils.getTimeHHmma(hours, minute));
    }

    private void setupStarterTimeAndDate() {
        long currentTime = System.currentTimeMillis();
        dateTxt.setText(DateUtils.getDateMMMMDYYYY(currentTime));
        timeTxt.setText(DateUtils.getTimeHHmma(currentTime));
    }

    private void initObservers() {

    }

    private void showPicker(int timePickerState, int datePickerState) {
        timePicker.setVisibility(timePickerState);
        calendarLayout.setVisibility(datePickerState);
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        onBackPressed();
    }

    @OnClick(R.id.saveBtn)
    public void save() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_DELIVERY_TIME_TYPE, deliveryTimeType);
        if (deliveryTimeType == TYPE_SPECIFIC_TIME) {
            resultIntent.putExtra(KEY_DELIVERY_TIME_DATE, dateTxt.getText().toString());
            resultIntent.putExtra(KEY_DELIVERY_TIME_TIME, timeTxt.getText().toString());
            resultIntent.putExtra(KEY_DELIVERY_TIME_TIMESTAMP, selectedTime);
        }
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @OnClick(R.id.asSoonBtn)
    public void clickAsSoon() {
        deliveryTimeType = TYPE_AS_SOON;
        specifTimeBtnIcon.setImageDrawable(getResources().getDrawable(R.mipmap.assoon_un_check));
        asSoonBtnIcon.setImageDrawable(getResources().getDrawable(R.mipmap.assoon_check));
        showPicker(View.GONE, View.GONE);
    }

    @OnClick(R.id.specifTimeBtn)
    public void clickSpecificTime() {
        deliveryTimeType = TYPE_SPECIFIC_TIME;
        specifTimeBtnIcon.setImageDrawable(getResources().getDrawable(R.mipmap.assoon_check));
        asSoonBtnIcon.setImageDrawable(getResources().getDrawable(R.mipmap.assoon_un_check));
        showPicker(View.GONE, View.VISIBLE);
    }

    @OnClick(R.id.dateTxt)
    public void clickDate() {
        showPicker(View.GONE, View.VISIBLE);
    }

    @OnClick(R.id.timeTxt)
    public void clickTime() {
        showPicker(View.VISIBLE, View.GONE);
    }

}
