package app.client.munchbear.munchbearclient.view.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.client.munchbear.munchbearclient.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PromoCodeFragment extends BaseFragment {

    @BindView(R.id.code) TextView codeTxt;

    private Unbinder unbinder;

    public static PromoCodeFragment newInstance() {
        PromoCodeFragment promoCodeFragment = new PromoCodeFragment();
        return promoCodeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.promo_code_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.closeBtn)
    public void closeFragment() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.copyBtn)
    public void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("promoCode", codeTxt.getText());
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
        getActivity().onBackPressed();
    }
}
