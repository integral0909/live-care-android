package app.client.munchbear.munchbearclient.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import app.client.munchbear.munchbearclient.R;

import static app.client.munchbear.munchbearclient.view.fragment.BaseFragment.INTENT_BUNDLE;

/**
 * Created by Roman on 7/5/2018.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected void blockUiIfNeeded(boolean isShouldShow) {
        if (isShouldShow) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    protected void openActivity(Class mClass, Bundle bundle) {
        Intent intent = new Intent(this, mClass);
        intent.putExtra(INTENT_BUNDLE, bundle);
        startActivity(intent);
    }

    protected void openActivityWithFinish(Class mClass, boolean clearBackStack) {
        Intent intent = new Intent(this, mClass);
        startActivity(intent);
        if (clearBackStack) {
            finishAffinity();
        } else {
            finish();
        }
    }

    protected void openChangeDataActivity(int changeType) {
        Intent intent = new Intent(this, ChangeDataActivity.class);
        intent.putExtra(ChangeDataActivity.CHANGE_TYPE, changeType);
        startActivityForResult(intent, ChangeDataActivity.CHANGE_DATA_REQUEST_CODE);
    }

    protected void replaceFragment(Fragment fragment, boolean addToBackStack, int idContainer, boolean showAnim, boolean bottomToTopAnim) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (showAnim) {
            if (bottomToTopAnim) {
                ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_in_down, R.anim.slide_in_down);
            } else {
                ft.setCustomAnimations(R.anim.enter_in_anim, R.anim.enter_out_anim, R.anim.exit_out_anim, R.anim.exit_in_anim);
            }
        }
        ft.replace(idContainer, fragment, fragment.getClass().getName());
        if (addToBackStack) {
            ft.addToBackStack(fragment.getClass().getName());
        }
        ft.commit();
    }

    protected Fragment getFragmentByTag(String className) {
        return getSupportFragmentManager().findFragmentByTag(className);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVisible(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setVisible(false);
    }
}
