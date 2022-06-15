package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.dish.Dish;
import app.client.munchbear.munchbearclient.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * @author Nazar V.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuItemViewHolder> {

    private DecimalFormat decim = new DecimalFormat("0.00");

    private MenuItemClickListener menuItemClickListener;
    private List<Dish> dishItemList = new ArrayList<>();
    private List<Dish> dishItemListCopy = new ArrayList<>();
    private Context context;

    public MenuAdapter(Context context, MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
        this.context = context;
    }

    @Override
    public MenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_menu_item, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuItemViewHolder holder, int position) {
        Dish dish = dishItemList.get(position);
        holder.bindData(dish);
    }

    @Override
    public int getItemCount() {
        return dishItemList.size();
    }

    public void refreshAdapter(List<Dish> dishItem) {
        if (dishItem != null) {
            this.dishItemList.clear();
            this.dishItemListCopy.clear();
            this.dishItemList.addAll(dishItem);
            this.dishItemListCopy.addAll(dishItem);
            notifyDataSetChanged();
        }
    }

    public void cleanData() {
        this.dishItemList.clear();
        this.dishItemListCopy.clear();
        notifyDataSetChanged();
    }

    public void filter(String query) {
        dishItemList.clear();
        if (query.isEmpty()) {
            dishItemList.addAll(dishItemListCopy);
        } else {
            String queryLowerCase = query.toLowerCase();
            for (Dish item : dishItemListCopy) {
                String itemTitle = item.getName();
                if (itemTitle.toLowerCase().contains(queryLowerCase)) {
                    dishItemList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class MenuItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvMenuItemTitle) TextView title;
        @BindView(R.id.tvMenuItemExplanation) TextView description;
        @BindView(R.id.tvMenuItemPrice) TextView itemPrice;
        @BindView(R.id.ivMenuItem) RoundedImageView itemImg;

        public MenuItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(Dish dish) {
            title.setText(dish.getName());
            description.setText(dish.getDescription());
            itemPrice.setText(String.format(context.getString(R.string.detail_dish_price_format),
                    decim.format(dish.getCostDollar())));
            ImageUtils.setImageFromUrl(dish.getImgUrl(), itemImg, R.mipmap.dish_default_avatart);
        }

        @OnClick(R.id.rootContainer)
        public void onRootClick(View v) {
            menuItemClickListener.onClick(dishItemList.get(getAdapterPosition()));
        }
    }

    public interface MenuItemClickListener {
        void onClick(Dish dish);
    }
}
