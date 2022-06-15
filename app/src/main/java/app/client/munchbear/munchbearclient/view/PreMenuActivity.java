package app.client.munchbear.munchbearclient.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.restaurant.AvailableRestaurants;
import app.client.munchbear.munchbearclient.view.fragment.PreMenuFragment;
import app.client.munchbear.munchbearclient.viewmodel.PreMenuActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/*
 * @author Nazar V.
 */
public class PreMenuActivity extends BaseActivity {

    @BindView(R.id.tvToolbarTitle)
    TextView tvTitle;
    @BindView(R.id.ivToolbarClose)
    ImageView ivClose;

    private Unbinder unbinder;
    private PreMenuActivityViewModel preMenuActivityViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_menu_layout);
        unbinder = ButterKnife.bind(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        preMenuActivityViewModel = ViewModelProviders.of(this).get(PreMenuActivityViewModel.class);

        replaceFragment(PreMenuFragment.newInstance(), true, R.id.container, false, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_in_down); //TODO Fix animation
    }

    @Override
    protected void onPause() {
        super.onPause();
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_in_down);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        AvailableRestaurants.getInstance().setNeedRefresh(true);
        if (getFragmentByTag(PreMenuFragment.class.getName()) instanceof PreMenuFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.ivToolbarClose)
    public void onBtnCloseClick(View v) {
        onBackPressed();
    }

}
