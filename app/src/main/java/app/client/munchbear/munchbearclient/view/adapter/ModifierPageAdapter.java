package app.client.munchbear.munchbearclient.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.interfaces.DishChangeSumListener;
import app.client.munchbear.munchbearclient.model.modifier.category.ExcludeModifierCategory;
import app.client.munchbear.munchbearclient.model.modifier.category.OptionalModifierCategory;
import app.client.munchbear.munchbearclient.view.fragment.ModifierListFragment;
import xyz.santeri.wvp.WrappingFragmentStatePagerAdapter;

public class ModifierPageAdapter extends WrappingFragmentStatePagerAdapter {

    private List<String> titleList;
    private List<OptionalModifierCategory> optionalList;
    private List<ExcludeModifierCategory> excludeList;
    private DishChangeSumListener dishChangeSumListener;
    private int excludeListSize;
    private int optionalListSize;

    public ModifierPageAdapter(FragmentManager fm, List<OptionalModifierCategory> categoryOptionalList,
                               List<ExcludeModifierCategory> categoryExcludeList, DishChangeSumListener dishChangeSumListener) {
        super(fm);
        this.optionalList = categoryOptionalList;
        this.excludeList = categoryExcludeList;
        this.titleList = getTabTitleList();
        this.dishChangeSumListener = dishChangeSumListener;
        this.excludeListSize = excludeList != null ? excludeList.size() : 0;
        this.optionalListSize = optionalList != null ? optionalList.size() : 0;
    }

    @Override
    public Fragment getItem(int position) {
        ModifierListFragment fragment = getModifierListFragment(position);
        fragment.setListener(dishChangeSumListener);
        return fragment;
    }

    private ModifierListFragment getModifierListFragment(int position) {
        if (isExclude(position)) {
            return ModifierListFragment.newInstance(excludeList.get(position), isExclude(position));
        } else {
            return ModifierListFragment.newInstance(optionalList.get(position - excludeListSize), isExclude(position));
        }
    }

    private boolean isExclude(int position) {
        return position + 1 <= excludeListSize;
    }

    private List<String> getTabTitleList() {
        List<String> tabTitleList = new ArrayList<>();
        if (excludeList != null && excludeList.size() > 0) {
            for (ExcludeModifierCategory category : excludeList) {
                tabTitleList.add(category.getName());
            }
        }

        if (optionalList != null && optionalList.size() > 0) {
            for (OptionalModifierCategory category : optionalList) {
                tabTitleList.add(category.getName());
            }
        }
        return tabTitleList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public int getCount() {
        return titleList.size();
    }
}
