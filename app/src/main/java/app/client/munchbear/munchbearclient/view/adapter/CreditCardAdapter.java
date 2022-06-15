package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.interfaces.CreditCardClickListener;
import app.client.munchbear.munchbearclient.model.payment.CreditCard;
import app.client.munchbear.munchbearclient.utils.CardBrand;
import app.client.munchbear.munchbearclient.utils.NetworkUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.PaymentCardViewHolder> {

    private Context context;
    private CreditCardClickListener cardClickListener;
    private List<CreditCard> creditCardList;
    private boolean disablePopUp = false;

    public CreditCardAdapter(Context context, List<CreditCard> creditCardList, CreditCardClickListener cardClickListener) {
        this.context = context;
        this.creditCardList = creditCardList;
        this.cardClickListener = cardClickListener;
    }

    @Override
    public PaymentCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);

        return new PaymentCardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PaymentCardViewHolder holder, int position) {
        holder.bindData(creditCardList.get(position));
    }

    @Override
    public int getItemCount() {
        return creditCardList.size();
    }

    private void removeItemAt(int position) {
        creditCardList.remove(position);
        notifyItemRemoved(position);
        checkEmptyList();
    }

    public void disablePopUp() {
        disablePopUp = true;
    }

    private void checkEmptyList() {
        if (creditCardList.size() == 0) {
            cardClickListener.hideCardListTitle();
        }
    }

    public class PaymentCardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lastCardNumbers) TextView lastCardNumbers;
        @BindView(R.id.cardBrand) ImageView cardBrandLogo;
        @BindView(R.id.dotsMore) ImageView dotsMore;

        public PaymentCardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(CreditCard creditCard) {
            lastCardNumbers.setText(creditCard.getLast_four());
            cardBrandLogo.setImageResource(CardBrand.getCartTypeByName(creditCard.getBrand()).getFrontResource());
            dotsMore.setVisibility(disablePopUp ? View.GONE : View.VISIBLE);
        }

        @OnClick(R.id.rootLayout)
        public void clickItem(View view) {
            if (disablePopUp) {
                cardClickListener.selectCard(creditCardList.get(getAdapterPosition()));
            } else {
                PopupMenu popupMenu = new PopupMenu(context, cardBrandLogo);
                popupMenu.getMenuInflater().inflate(R.menu.card_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.edit:
                            cardClickListener.editCard(creditCardList.get(getAdapterPosition()));
                            break;
                        case R.id.delete:
                            if (NetworkUtils.isConnectionAvailable(context)) {
                                cardClickListener.deleteCard(creditCardList.get(getAdapterPosition()).getId());
                                removeItemAt(getAdapterPosition());
                            }
                            break;
                    }

                    return true;
                });
                popupMenu.show();
            }
        }

    }
}
