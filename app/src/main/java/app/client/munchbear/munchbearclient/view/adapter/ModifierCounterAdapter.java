package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.DishChangeSumListener;
import app.client.munchbear.munchbearclient.model.modifier.OptionalModifier;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifierCounterAdapter extends RecyclerView.Adapter<ModifierCounterAdapter.ModifierCounterViewHolder> {

    private final int MAX_COUNT = 99;

    private DecimalFormat decim = new DecimalFormat("0.00");
    private Context context;
    private Resources resources;
    private List<OptionalModifier> modifierList;
    private DishChangeSumListener dishChangeSumListener;

    public ModifierCounterAdapter(Context context, List<OptionalModifier> modList, DishChangeSumListener dishChangeSumListener) {
        this.context = context;
        this.modifierList = modList;
        this.dishChangeSumListener = dishChangeSumListener;
    }

    @Override
    public ModifierCounterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.modifier_counter_item, parent, false);

        resources = context.getResources();

        return new ModifierCounterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ModifierCounterViewHolder holder, int position) {
        holder.bindData(modifierList.get(position));
    }

    @Override
    public int getItemCount() {
        return modifierList.size();
    }

    public class ModifierCounterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.modifierName) TextView modifierName;
        @BindView(R.id.price) TextView price;
        @BindView(R.id.modifierCount) TextView modifierCountTxt;

        private OptionalModifier modifier;

        public ModifierCounterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(OptionalModifier modifier) {
            this.modifier = modifier;
            modifierName.setText(modifier.getName());
            modifierCountTxt.setText(String.valueOf(modifier.getCounter()));
            setupPrice(modifier);
            checkCounterTextColor();
        }

        private void setupPrice(OptionalModifier modifier) {
            price.setText("+$" + decim.format(modifier.getPriceInDollar(false)));
            price.setVisibility(modifier.getPriceCent() == 0 ? View.INVISIBLE : View.VISIBLE);
        }

        private void checkCounterTextColor() {
            modifierCountTxt.setTextColor(modifier.getCounter() == 0 ? resources.getColor(R.color.darkGrey)
                    : resources.getColor(R.color.colorPrimary));
        }

        private void changeModifierData(int count, boolean select) {
            modifier.setCounter(count);
            modifier.setSelected(select);
            modifierCountTxt.setText(String.valueOf(count));
            checkCounterTextColor();

            dishChangeSumListener.onChangeTotalSum();
        }

        @OnClick(R.id.btn_plus)
        public void addDishCount() {
            if (modifier.getCounter() < MAX_COUNT) {
                int modifierCount = modifier.getCounter();
                modifierCount++;
                changeModifierData(modifierCount, true);
            }
        }

        @OnClick(R.id.btn_minus)
        public void subDishCount() {
            int modifierCount = modifier.getCounter();
            modifierCount--;
            if (modifier.getCounter() == 1) {
                changeModifierData(modifierCount, false);
            }
            if (modifier.getCounter() > 1) {
                changeModifierData(modifierCount, true);
            }
        }
    }

}
