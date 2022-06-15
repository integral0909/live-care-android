package app.client.munchbear.munchbearclient.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import app.client.munchbear.munchbearclient.view.adapter.MenuAdapter;

/**
 * Created by Roman on 7/5/2018.
 */
public class BaseFragment extends Fragment {

    public static final String INTENT_BUNDLE = "intent.bundle";

    protected void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    protected void openActivity(Context context, Class mClass) {
        Intent intent = new Intent(context, mClass);
        startActivity(intent);
    }

    protected void openActivity(Context context, Class mClass, Bundle bundle) {
        Intent intent = new Intent(context, mClass);
        intent.putExtra(INTENT_BUNDLE, bundle);
        startActivity(intent);
    }

    protected void replaceFragment(Fragment fragment, boolean addToBackStack, int idContainer) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(idContainer, fragment, fragment.getClass().getName());
        if (addToBackStack) {
            ft.addToBackStack(fragment.getClass().getName());
        }
        ft.commitAllowingStateLoss();
    }

}
