package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.SliderPromoCode;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SliderRVAdapter extends RecyclerView.Adapter<SliderRVAdapter.SliderViewHolder> {

    private List<SliderPromoCode> sliderPromoCodeList;
    private Context context;
    private PromoCodeOnClickListener promoCodeOnClickListener;

    public SliderRVAdapter(Context context, List<SliderPromoCode> sliderPromoCodes, PromoCodeOnClickListener listener) {
        this.context = context;
        this.sliderPromoCodeList = sliderPromoCodes;
        this.promoCodeOnClickListener = listener;
    }

    @Override
    public SliderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_item, parent, false);

        return new SliderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SliderViewHolder holder, int position) {
        holder.bindData(sliderPromoCodeList.get(position));
    }

    public interface PromoCodeOnClickListener {
        void onItemPromoCodeClick(int position);
    }

    @Override
    public int getItemCount() {
        return sliderPromoCodeList.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {

        public SliderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(SliderPromoCode sliderPromoCode) {

        }

        @OnClick(R.id.getCodeBtn)
        public void onPromoCodeClick() {
            promoCodeOnClickListener.onItemPromoCodeClick(this.getLayoutPosition());
        }

    }

}
