package com.dab.medireminder.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dab.medireminder.R;
import com.dab.medireminder.data.model.Medicine;
import com.dab.medireminder.utils.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class MedicineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity activity;
    private final List<Medicine> itemList;
    private final MedicineListener itemListener;

    public MedicineAdapter(Activity activity, List<Medicine> itemList, MedicineListener itemListener) {
        this.activity = activity;
        this.itemList = itemList;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
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

        itemHolder.btnDelete.setOnClickListener(v -> itemListener.onClickDeleteMedicine(medicine, position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView ivIcon;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_dose)
        TextView tvDose;

        @BindView(R.id.btn_delete)
        AppCompatImageView btnDelete;

        @BindView(R.id.card_image)
        CardView cardImage;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cardImage.getLayoutParams();
            layoutParams.height = displayMetrics.widthPixels / 2 - ViewUtils.convertDpToPixel(25, activity);
            cardImage.setLayoutParams(layoutParams);
        }

    }

    public interface MedicineListener {
        void onClickMedicine(Medicine medicine);

        void onClickDeleteMedicine(Medicine medicine, int position);
    }

}

