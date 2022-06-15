package app.client.munchbear.munchbearclient.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import app.client.munchbear.munchbearclient.R;
import app.client.munchbear.munchbearclient.model.modifier.ExcludeModifier;
import app.client.munchbear.munchbearclient.model.modifier.Modifier;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifierCheckBoxAdapter extends RecyclerView.Adapter<ModifierCheckBoxAdapter.ModifierCheckBoxViewHolder>{

    private Context context;
    private List<ExcludeModifier> modifierList;

    public ModifierCheckBoxAdapter(Context context, List<ExcludeModifier> modList) {
        this.context = context;
        this.modifierList = modList;
    }

    @Override
    public ModifierCheckBoxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.modifier_check_box_item, parent, false);

        return new ModifierCheckBoxViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ModifierCheckBoxAdapter.ModifierCheckBoxViewHolder holder, int position) {
        holder.bindData(modifierList.get(position));
    }

    @Override
    public int getItemCount() {
        return modifierList.size();
    }

    public class ModifierCheckBoxViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.modifierName) TextView modifierName;
        @BindView(R.id.checkBox) CheckBox checkBox;

        private Modifier modifier;

        public ModifierCheckBoxViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bindData(Modifier modifier) {
            this.modifier = modifier;
            modifierName.setText(modifier.getName());
            checkBox.setChecked(modifier.isSelected());
        }

        @OnClick(R.id.checkBox)
        public void clickCheckBox(View view) {
            modifier.setSelected(checkBox.isChecked());
        }
    }

}
