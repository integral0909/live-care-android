package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.order.created.CreatedOrderItems;
import app.client.munchbear.munchbearclient.utils.ImageUtils;
import app.client.munchbear.munchbearclient.viewmodel.CartActivityViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;

import static app.client.munchbear.munchbearclient.view.SelectPayMethodActivity.PAYMENT_METHOD_LOYALTY;

/**
 * Created by Roman on 7/21/2018.
 */
public class CreatedOrderDishAdapter extends RecyclerView.Adapter<CreatedOrderDishAdapter.CartDishViewHolder> {

    private Context context;
    private List<CreatedOrderItems> dishList;
    private String paymentType;

    public CreatedOrderDishAdapter(Context cont, String paymentType, List<CreatedOrderItems> dishList) {
        this.context = cont;
        this.paymentType = paymentType;
        this.dishList = dishList;
    }

    @Override
    public CartDishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.created_order_dish_item, parent, false);

        return new CartDishViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartDishViewHolder holder, int position) {
        holder.bindData(dishList.get(position));
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public class CartDishViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageStar) ImageView imageStar;
        @BindView(R.id.dishImage) RoundedImageView dishImage;
        @BindView(R.id.dishName) TextView dishName;
        @BindView(R.id.dishSize) TextView dishSize;
        @BindView(R.id.mandatoryModifierTxt) TextView mandatoryModifierTxt;
        @BindView(R.id.counterModifierTxt) TextView optionalModifierTxt;
        @BindView(R.id.excludeModifierTxt) TextView excludeModifierTxt;
        @BindView(R.id.totalSumDollar) TextView totalSumDollar;
        @BindView(R.id.totalSumPoint) TextView totalSumPoint;

        private DecimalFormat decim = new DecimalFormat("0.00");

        private CreatedOrderItems orderItem;
        private Resources resources;

        public CartDishViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(CreatedOrderItems orderItem) {
            this.orderItem = orderItem;
            resources = context.getResources();

            initDishName();
            setupOrderTotal();

            dishSize.setText(String.format(resources.getString(R.string.cart_dish_size_format), orderItem.getCreatedOrderSize().getName()));
            ImageUtils.setImageFromUrl(orderItem.getCreatedOrderSize().getCreatedOrderDish().getImage(), dishImage, R.mipmap.dish_default_avatart);

            initModifierField(mandatoryModifierTxt, orderItem.getAllMandatoryNames(), "Mandatory: ");
            initModifierField(optionalModifierTxt, orderItem.getAllOptionalNames(), "Optional: ");
            initModifierField(excludeModifierTxt, orderItem.getAllExcludeNames(), "Exclude: ");
        }

        private void setupOrderTotal() {
            String totalDollarStr = "$" + decim.format(orderItem.getTotalInDollar());

            totalSumDollar.setVisibility(isPaymentLoyaltyPoints() ? View.GONE : View.VISIBLE);
            totalSumPoint.setVisibility(isPaymentLoyaltyPoints() ? View.VISIBLE : View.GONE);
            imageStar.setVisibility(isPaymentLoyaltyPoints() ? View.VISIBLE : View.GONE);

            totalSumPoint.setText(String.valueOf(orderItem.getTotal()));
            totalSumDollar.setText(totalDollarStr);
        }

        private boolean isPaymentLoyaltyPoints() {
            return paymentType.equals(PAYMENT_METHOD_LOYALTY);
        }

        private void initModifierField(TextView modifierTv, String allModifiersName, String prefix) {
            if (!TextUtils.isEmpty(allModifiersName)) {
                String resultStr = prefix + allModifiersName;
                modifierTv.setText(resultStr);
            } else {
                modifierTv.setVisibility(View.GONE);
            }
        }

        private void initDishName() {
            String name = orderItem.getCreatedOrderSize().getCreatedOrderDish().getName();
            String count = String.valueOf(orderItem.getQuantity());
            dishName.setText(orderItem.getQuantity() > 1 ? String.format(resources.getString(R.string.cart_dish_name_many_format),
                    name, count) : name);
        }
    }
}
