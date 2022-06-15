package app.client.munchbear.munchbearclient.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.viewmodel.ChangeDataActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class ChangeDataActivity extends BaseActivity {

    public static final String CHANGE_TYPE = "change.type";
    public static final String RESULT_VALUE = "result.value";
    public static final int CHANGE_DATA_REQUEST_CODE = 101;
    public static final int CHANGE_AMOUNT_OF_PEOPLE = 1;
    public static final int CHANGE_CHILD_CHAIR = 2;
    public static final int CHANGE_PROMOCODE = 3;
    public static final int CHANGE_NUMBER_OF_TABLE = 4;
    public static final int CHANGE_COMMENT = 5;

    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.dataTextInputLayout) TextInputLayout dataTextInputLayout;

    private ChangeDataActivityViewModel changeDataActivityViewModel;
    private int changeType;
    private String changedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_data);
        ButterKnife.bind(this);

        getChangeType();
        initViews();
        setInputType();

    }

    private void getChangeType() {
        if (getIntent().hasExtra(CHANGE_TYPE)) {
            changeType = getIntent().getIntExtra(CHANGE_TYPE, 1);
        }
    }

    private void initViews() {
        switch (changeType) {
            case CHANGE_AMOUNT_OF_PEOPLE:
                setTextDataToViews(getResources().getString(R.string.change_data_amount_people));
                break;
            case CHANGE_CHILD_CHAIR:
                setTextDataToViews(getResources().getString(R.string.change_data_child_chair));
                break;
            case CHANGE_NUMBER_OF_TABLE:
                setTextDataToViews(getResources().getString(R.string.change_data_table_number));
                break;
            case CHANGE_PROMOCODE:
                setTextDataToViews(getResources().getString(R.string.change_data_promo_code));
                break;
            case CHANGE_COMMENT:
                setTextDataToViews(getResources().getString(R.string.change_data_comment));
                break;
        }

    }

    private void setInputType() {
        if (dataTextInputLayout.getEditText() != null) {
            dataTextInputLayout.getEditText().setInputType(changeType == CHANGE_COMMENT ? InputType.TYPE_CLASS_TEXT
                     : InputType.TYPE_CLASS_NUMBER);
        }
    }

    private void setTextDataToViews(String toolbarTxt) {
        toolbarTitle.setText(toolbarTxt);
        dataTextInputLayout.setHint(toolbarTxt);
    }

    @OnClick(R.id.saveBtn)
    public void clickSave(View view) {
        //TODO Validate data (table etc.)
        Intent resultIntent = new Intent();
        if (dataTextInputLayout.getEditText() != null) {
            changedData = dataTextInputLayout.getEditText().getText().toString().trim()
                    .replaceFirst("^0+(?!$)", "");
            if (isDataValid()) {
                resultIntent.putExtra(RESULT_VALUE, changedData);
                resultIntent.putExtra(CHANGE_TYPE, changeType);
                setResult(RESULT_OK, resultIntent);
                hideKeyboardFrom(this, view);
                finish();
            }
        }
    }

    private boolean isDataValid(){
        switch (changeType) {
            case CHANGE_AMOUNT_OF_PEOPLE: return isCountOfPeopleValid();
            default: return true;
        }
    }

    private boolean isCountOfPeopleValid() {
        if (changedData.length() > 0 && !changedData.equals("0")) {
            return true;
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_count_of_people_error), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @OnClick(R.id.closeBtn)
    public void close() {
        onBackPressed();
    }

    @OnFocusChange(R.id.rootLayout)
    public void clickRootLayout(View view) {
        hideKeyboardFrom(this, view);
    }

}
