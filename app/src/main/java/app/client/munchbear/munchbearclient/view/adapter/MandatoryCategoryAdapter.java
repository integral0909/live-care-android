package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.DishChangeSumListener;
import app.client.munchbear.munchbearclient.model.modifier.category.MandatoryModifierCategory;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MandatoryCategoryAdapter extends RecyclerView.Adapter<MandatoryCategoryAdapter.MandatoryModifierListViewHolder> {

    private Context context;
    private List<MandatoryModifierCategory> mandatoryModifierCategoryList;
    private DishChangeSumListener dishChangeSumListener;

    public MandatoryCategoryAdapter(Context context, List<MandatoryModifierCategory> mandatoryModifierCategoryList,
                                    DishChangeSumListener dishChangeSumListener) {
        this.context = context;
        this.mandatoryModifierCategoryList = mandatoryModifierCategoryList;
        this.dishChangeSumListener = dishChangeSumListener;
    }

    @Override
    public MandatoryModifierListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_mandatory_category_item, parent, false);

        return new MandatoryModifierListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MandatoryModifierListViewHolder holder, int position) {
        holder.bindData(mandatoryModifierCategoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return mandatoryModifierCategoryList != null ? mandatoryModifierCategoryList.size() : 0;
    }

    public class MandatoryModifierListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.mandatoryCategoryTitle) TextView mandatoryCategoryTitle;
        @BindView(R.id.mandatoryCategoryRV) RecyclerView mandatoryCategoryRV;

        private MandatoryModifierAdapter mandatoryModifierAdapter;
        private MandatoryModifierCategory modifierCategory;

        public MandatoryModifierListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bindData(MandatoryModifierCategory category) {
            modifierCategory = category;

            if (mandatoryModifierAdapter == null) {
                setupModifierList();
            }
            mandatoryCategoryTitle.setText(modifierCategory.getName());
        }

        private void setupModifierList() {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            mandatoryModifierAdapter = new MandatoryModifierAdapter(context, modifierCategory.getMandatoryModifierList(),
                    modifierCategory.getMaxAmount(), dishChangeSumListener);
            mandatoryCategoryRV.setLayoutManager(layoutManager);
            mandatoryCategoryRV.setItemAnimator(new DefaultItemAnimator());
            mandatoryCategoryRV.setAdapter(mandatoryModifierAdapter);
        }

    }
}
