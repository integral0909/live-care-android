package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.view.fragment.MyOrderListFragment;

public class MyOrdersPageAdapter extends FragmentStatePagerAdapter {

    private static final int PAGE_COUNT = 2;

    private Context context;
    public MyOrdersPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public MyOrderListFragment getItem(int position) {
        if (position == 0) {
            return MyOrderListFragment.newInstance(false);
        } else {
            return MyOrderListFragment.newInstance(true);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.my_orders_tab_current);
        } else {
            return context.getString(R.string.my_orders_tab_reservation);
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
