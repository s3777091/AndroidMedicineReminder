package com.dab.medireminder.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dab.medireminder.R;
import com.dab.medireminder.data.model.Medicine;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class SuggestMedicineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity activity;
    private final List<Medicine> itemList;
    private final MedicineListener itemListener;

    public SuggestMedicineAdapter(Activity activity, List<Medicine> itemList, MedicineListener itemListener) {
        this.activity = activity;
        this.itemList = itemList;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggest_medicine, parent, false);
        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final RecyclerViewHolder itemHolder = (RecyclerViewHolder) holder;
        final Medicine medicine = itemList.get(position);

        itemHolder.tvName.setText(medicine.getName());
        itemHolder.tvDose.setText(medicine.getDose());

        if (TextUtils.isEmpty((medicine.getImage()))) {
            itemHolder.ivIcon.setImageResource(R.drawable.img_medicine);
        } else {
            Glide.with(activity).load(medicine.getImage()).into(itemHolder.ivIcon);
        }

        itemHolder.itemView.setOnClickListener(v -> itemListener.onClickMedicine(medicine));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_dose)
        TextView tvDose;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface MedicineListener {
        void onClickMedicine(Medicine medicine);
    }

}

