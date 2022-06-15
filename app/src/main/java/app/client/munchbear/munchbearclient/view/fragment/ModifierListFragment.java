package app.client.munchbear.munchbearclient.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.DishChangeSumListener;
import app.client.munchbear.munchbearclient.model.modifier.category.ExcludeModifierCategory;
import app.client.munchbear.munchbearclient.model.modifier.category.OptionalModifierCategory;
import app.client.munchbear.munchbearclient.view.adapter.ModifierCounterAdapter;
import app.client.munchbear.munchbearclient.view.adapter.ModifierCheckBoxAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ModifierListFragment extends BaseFragment {

    @BindView(R.id.modifierRV) RecyclerView modifierRV;

    private static final String MODIFIER_CATEGORY = "modifier.category";
    private static final String MODIFIER_IS_EXCLUDE = "modifier.is.exclude";

    private Unbinder unbinder;
    private ExcludeModifierCategory excludeModifierCategory;
    private OptionalModifierCategory optionalModifierCategory;
    private DishChangeSumListener dishChangeSumListener;
    private RecyclerView.Adapter modifierAdapter;
    private boolean isExclude;

    public static ModifierListFragment newInstance(ExcludeModifierCategory excludeModifierCategory, boolean isExclude) {
        ModifierListFragment fragment = new ModifierListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MODIFIER_IS_EXCLUDE, isExclude);
        bundle.putParcelable(MODIFIER_CATEGORY, excludeModifierCategory);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ModifierListFragment newInstance(OptionalModifierCategory optionalModifierCategory, boolean isExclude) {
        ModifierListFragment fragment = new ModifierListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MODIFIER_IS_EXCLUDE, isExclude);
        bundle.putParcelable(MODIFIER_CATEGORY, optionalModifierCategory);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBundleData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modifier_list_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        initModifierList();

        return view;
    }

    public void setListener(DishChangeSumListener listener) {
        dishChangeSumListener = listener;
    }

    private void getBundleData() {
        Bundle bundle = getArguments();
        isExclude = bundle.getBoolean(MODIFIER_IS_EXCLUDE);

        if (isExclude) {
            excludeModifierCategory = bundle.getParcelable(MODIFIER_CATEGORY);
        } else {
            optionalModifierCategory = bundle.getParcelable(MODIFIER_CATEGORY);
        }
    }

    private void initModifierList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        initModifierAdapter();
        modifierRV.setLayoutManager(layoutManager);
        modifierRV.setItemAnimator(new DefaultItemAnimator());
        modifierRV.setAdapter(modifierAdapter);

    }

    private void initModifierAdapter() {
        if (isExclude) {
            modifierAdapter = new ModifierCheckBoxAdapter(getContext(), excludeModifierCategory.getExcludeModifierList());
        } else {
            modifierAdapter = new ModifierCounterAdapter(getContext(), optionalModifierCategory.getOptionalModifierList(),
                    dishChangeSumListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
