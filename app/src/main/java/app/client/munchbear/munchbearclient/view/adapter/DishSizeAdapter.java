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
import app.client.munchbear.munchbearclient.model.dish.DishSize;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishSizeAdapter extends RecyclerView.Adapter<DishSizeAdapter.DishSizeViewHolder> {

    private Context context;
    private DecimalFormat decim = new DecimalFormat("0.00");
    private List<DishSize> dishSizeList;
    private int prevPosition = 0;
    private DishChangeSumListener dishChangeSumListener;

    public DishSizeAdapter(Context context, List<DishSize> dishSizes, DishChangeSumListener dishChangeSumListener) {
        this.context = context;
        this.dishSizeList = dishSizes;
        this.dishChangeSumListener = dishChangeSumListener;
    }

    @Override
    public DishSizeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_size_item, parent, false);

        return new DishSizeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DishSizeViewHolder holder, int position) {
        getSelectedItem();
        holder.bindData(dishSizeList.get(position));
    }

    @Override
    public int getItemCount() {
        return dishSizeList.size();
    }

    private void getSelectedItem() {
        for (int i = 0; i < dishSizeList.size(); i++) {
            DishSize dishSize = dishSizeList.get(i);
            if (dishSize.isSelected() && i != prevPosition) {
                prevPosition = i;
                break;
            }
        }
    }

    public class DishSizeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sizeItemTitle) TextView sizeItemTitle;
        @BindView(R.id.sizeItemRoot) LinearLayout sizeItemRoot;

        public DishSizeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(DishSize dishSize) {
            sizeItemTitle.setText(String.format(context.getResources().getString(R.string.detail_dish_size_text),
                    dishSize.getSizeTitle(), decim.format(dishSize.getPriceInDouble(true))));
            changeViewColor(dishSize);
        }

        @OnClick(R.id.sizeItemRoot)
        public void clickItemRoot() {
            changePreviousItem();
            DishSize dishSize = dishSizeList.get(getAdapterPosition());
            dishSize.setSelected(true);
            changeViewColor(dishSize);
            prevPosition = getAdapterPosition();

            dishChangeSumListener.onChangeTotalSum();
        }

        private void changePreviousItem() {
            DishSize prevDishSize = dishSizeList.get(prevPosition);
            prevDishSize.setSelected(false);
            notifyItemChanged(prevPosition, prevDishSize);
        }

        private void changeViewColor(DishSize dishSize) {
            Resources resources = context.getResources();
            sizeItemRoot.setBackground(dishSize.isSelected() ? resources.getDrawable(R.drawable.corner_size_item_green)
                    : resources.getDrawable(R.drawable.corner_btn_grey_size));
            sizeItemTitle.setTextColor(dishSize.isSelected() ? resources.getColor(R.color.colorWhite)
                    : resources.getColor(R.color.darkGrey));
        }
    }
}
