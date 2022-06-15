package app.client.munchbear.munchbearclient.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.view.fragment.SubMenuFragment;

/*
 * @author Nazar V.
 */
public class MenuViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<Integer> dishCategoryIds;

    public MenuViewPagerAdapter(FragmentManager fm, List<Integer> dishCategoryIds) {
        super(fm);
        this.dishCategoryIds = dishCategoryIds;
        initFragmentList();
    }

    private void initFragmentList() {
        for (int id : dishCategoryIds) {
            fragmentList.add(SubMenuFragment.newInstance(id));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return dishCategoryIds.size();
    }
}
