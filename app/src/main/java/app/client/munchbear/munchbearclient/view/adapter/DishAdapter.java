package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.dish.DishCategory;
import app.client.munchbear.munchbearclient.utils.DishHelper;
import app.client.munchbear.munchbearclient.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Roman on 11/5/2018.
 */
public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {

    private List<DishCategory> dishCategoryList;
    private Context mContext;
    private ClickDishItem clickDishItemListener;

    public DishAdapter(Context context, List<DishCategory> dishCategories, ClickDishItem listener) {
        this.mContext = context;
        this.dishCategoryList = dishCategories;
        this.clickDishItemListener = listener;
    }

    @Override
    public DishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dish_item, parent, false);

        return new DishViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DishViewHolder holder, int position) {
        holder.bindData(dishCategoryList.get(position));
    }

    public interface ClickDishItem {
        void dishItemClick(int position);
    }

    @Override public int getItemCount() {
        return dishCategoryList.size();
    }

    public class DishViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dishTitle) TextView dishTitle;
        @BindView(R.id.dishImage) RoundedImageView dishImg;

        public DishViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(DishCategory dishCategory) {
            dishTitle.setText(dishCategory.getName());
            ImageUtils.setImageFromUrl(dishCategory.getImageUrl(), dishImg, R.mipmap.dish_default_avatart);
        }

        @OnClick(R.id.rootLayout)
        public void clickRootBtn() {
            DishHelper.reselectDishCategory(getAdapterPosition(), dishCategoryList);
            clickDishItemListener.dishItemClick(getAdapterPosition());
        }
    }

}
