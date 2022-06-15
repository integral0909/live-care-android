package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.DishChangeSumListener;
import app.client.munchbear.munchbearclient.model.modifier.MandatoryModifier;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MandatoryModifierAdapter extends RecyclerView.Adapter<MandatoryModifierAdapter.MandatoryModifierViewHolder> {

    private Context context;
    private List<MandatoryModifier> mandatoryModifiersList;
    private DecimalFormat decim = new DecimalFormat("0.00");
    private DishChangeSumListener dishChangeSumListener;
    private int maxSelected;
    private int selectedCount;
    private int prevSelectedPos = 0;

    public MandatoryModifierAdapter(Context context, List<MandatoryModifier> modifiers, int maxSelected, DishChangeSumListener dishChangeSumListener) {
        this.context = context;
        this.mandatoryModifiersList = modifiers;
        this.maxSelected = maxSelected;
        this.dishChangeSumListener = dishChangeSumListener;
        this.selectedCount = getSelectedItemCount();
    }

    @Override
    public MandatoryModifierViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_size_item, parent, false);

        return new MandatoryModifierViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MandatoryModifierViewHolder holder, int position) {
        holder.bindData(mandatoryModifiersList.get(position));
    }

    private int getSelectedItemCount() {
        int count = 0;
        for (int i = 0; i < mandatoryModifiersList.size(); i++) {
            MandatoryModifier modifier = mandatoryModifiersList.get(i);
            if (modifier.isSelected()) {
                count++;
                prevSelectedPos = i;
            }
        }
        return count;
    }

    @Override
    public int getItemCount() {
        return mandatoryModifiersList.size();
    }

    public class MandatoryModifierViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sizeItemTitle) TextView modifierItemTitle;
        @BindView(R.id.sizeItemRoot) LinearLayout modifierItemRoot;

        public MandatoryModifierViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bindData(MandatoryModifier modifier) {
            setupTitle(modifier);
            changeViewColor(modifier);
        }

        private void setupTitle(MandatoryModifier modifier) {
            if (modifier.getPriceCent() == 0) {
                modifierItemTitle.setText(modifier.getName());
            } else {
                modifierItemTitle.setText(String.format(context.getResources().getString(R.string.detail_dish_size_text),
                        modifier.getName(), decim.format(modifier.getPriceInDollar())));
            }
        }

        @OnClick(R.id.sizeItemRoot)
        public void clickItemRoot() {
            MandatoryModifier modifier = mandatoryModifiersList.get(getAdapterPosition());
            handleItemClick(modifier);
            changeViewColor(modifier);
            dishChangeSumListener.onChangeTotalSum();

        }

        private void handleItemClick(MandatoryModifier modifier) {
            if (maxSelected == 1) {
                changePreviousItem();
                prevSelectedPos = getAdapterPosition();
                modifier.setSelected(true);
            } else {
                if (modifier.isSelected()) {
                    if (selectedCount > 1) {
                        modifier.setSelected(false);
                        selectedCount-- ;
                    }
                } else {
                    if (selectedCount < maxSelected) {
                        modifier.setSelected(true);
                        selectedCount++;
                    }
                }
            }
        }

        private void changePreviousItem() {
            MandatoryModifier prevModifier = mandatoryModifiersList.get(prevSelectedPos);
            prevModifier.setSelected(false);
            notifyItemChanged(prevSelectedPos, prevModifier);
        }

        private void changeViewColor(MandatoryModifier modifier) {
            Resources resources = context.getResources();
            modifierItemRoot.setBackground(modifier.isSelected() ? resources.getDrawable(R.drawable.corner_size_item_green)
                    : resources.getDrawable(R.drawable.corner_btn_grey_size));
            modifierItemTitle.setTextColor(modifier.isSelected() ? resources.getColor(R.color.colorWhite)
                    : resources.getColor(R.color.darkGrey));
        }

    }

}
