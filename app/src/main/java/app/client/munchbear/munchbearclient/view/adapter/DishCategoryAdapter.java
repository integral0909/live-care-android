package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.dish.DishCategory;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DishCategoryAdapter extends RecyclerView.Adapter<DishCategoryAdapter.DishCategoryViewHolder> {

    private Context context;
    private CategoryItemClickListener categoryItemClickListener;
    private List<DishCategory> dishCategoryList;
    private int lastPosition = 0;

    public DishCategoryAdapter(Context context, CategoryItemClickListener listener) {
        this.context = context;
        this.categoryItemClickListener = listener;
        this.dishCategoryList = new ArrayList<>();
    }

    @Override
    public DishCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_dish_category_item, parent, false);

        return new DishCategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DishCategoryViewHolder holder, int position) {
        holder.bindData(dishCategoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return dishCategoryList.size();
    }

    public void refreshAdapter(List<DishCategory> dishCategoryList, int selectedPosition) {
        this.lastPosition = selectedPosition;
        this.dishCategoryList.clear();
        this.dishCategoryList.addAll(dishCategoryList);
        changeCategoriesStatus(selectedPosition, lastPosition);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.dishCategoryList.clear();
        notifyDataSetChanged();
    }

    public void changeCategoriesStatus(int currentPosition, int lastPosition) {
        if (dishCategoryList != null && dishCategoryList.size() > 0) {
            dishCategoryList.get(currentPosition).setSelected(true);
            if (currentPosition != lastPosition) {
                dishCategoryList.get(lastPosition).setSelected(false);
            }
            this.lastPosition = currentPosition;
            notifyDataSetChanged();
        }

    }

    public interface CategoryItemClickListener {
        void categoryItemClick(int position);
    }

    public class DishCategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.menuBtnFilter)
        Button categoryBtn;

        public DishCategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(DishCategory category) {
            categoryBtn.setText(category.getName());
            changeBtnStyle(category.isSelected());
        }

        public void changeBtnStyle(boolean isSelect) {
            Resources resources = context.getResources();
            categoryBtn.setBackground(isSelect ? resources.getDrawable(R.drawable.menu_btn_filter_background_selected)
                    : resources.getDrawable(R.drawable.menu_btn_filter_background));
            categoryBtn.setHeight(isSelect ? 440 : 400);
            categoryBtn.setTextColor(isSelect ? resources.getColor(R.color.colorWhite) : resources.getColor(R.color.darkGrey));
        }

        @OnClick(R.id.menuBtnFilter)
        public void clickMenuItem() {
            categoryItemClickListener.categoryItemClick(getAdapterPosition());
            changeCategoriesStatus(getAdapterPosition(), lastPosition);
        }

    }

}
