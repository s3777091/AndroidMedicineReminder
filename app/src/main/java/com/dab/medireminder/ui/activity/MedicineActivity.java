package com.dab.medireminder.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;
import com.dab.medireminder.data.DBApp;
import com.dab.medireminder.data.model.Medicine;
import com.dab.medireminder.ui.adapter.MedicineAdapter;
import com.dab.medireminder.utils.AppUtils;
import com.dab.medireminder.utils.RecyclerUtils;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


@SuppressLint("NonConstantResourceId")
public class MedicineActivity extends BaseActivity implements MedicineAdapter.MedicineListener {

    public static final int REQUEST_ADD_MEDICINE = 200;
    public static final int RESULT_ADD_MEDICINE = 201;


    @BindView(R.id.rv_timer)
    RecyclerView rvTimer;

    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    @BindView(R.id.ll_no_data)
    View llNoData;

    @BindView(R.id.btn_add_medicine)
    FloatingActionButton btnAddMedicine;

    private DBApp dbApp;
    private MedicineAdapter medicineAdapter;

    private List<Medicine> medicineItem;

    @Override
    public int getLayout() {
        return R.layout.activity_medicine;
    }

    @Override
    public void initView() {
        RecyclerUtils.setupGridRecyclerView(this, rvTimer, 2);
    }

    @Override
    public void setEvents() {
    }

    @Override
    public void setData() {
        dbApp = new DBApp(this);
        loadData();
    }

    private void loadData() {
        pbLoading.setVisibility(View.VISIBLE);
        medicineItem = new ArrayList<>();
        medicineItem = dbApp.getMedicine();

        if (medicineItem == null || medicineItem.size() == 0) {
            llNoData.setVisibility(View.VISIBLE);
            rvTimer.setVisibility(View.GONE);
            btnAddMedicine.setVisibility(View.GONE);
        } else {
            llNoData.setVisibility(View.GONE);
            rvTimer.setVisibility(View.VISIBLE);
            btnAddMedicine.setVisibility(View.VISIBLE);
        }

        medicineAdapter = new MedicineAdapter(this, medicineItem, this);
        rvTimer.setAdapter(medicineAdapter);
        pbLoading.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_close)
    public void closeScreen() {
        finish();
    }

    @OnClick({R.id.btn_add_medicine, R.id.btn_timer_new})
    public void addNewMedicine() {
        startActivityForResult(new Intent(this, AddMedicineActivity.class), REQUEST_ADD_MEDICINE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_MEDICINE && resultCode == RESULT_ADD_MEDICINE) {
            loadData();
        }
    }

    @Override
    public void onClickMedicine(Medicine medicine) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.alert_preview_medicine, null);
        PhotoView photoView = mView.findViewById(R.id.photo_view);
        CardView btnClose = mView.findViewById(R.id.btn_close);
        TextView tvName = mView.findViewById(R.id.tv_name);
        TextView tvDose = mView.findViewById(R.id.tv_dose);

        tvName.setText(medicine.getName());
        tvDose.setText(medicine.getDose());

        if (!TextUtils.isEmpty((medicine.getImage()))) {
            Glide.with(this).load(medicine.getImage()).into(photoView);
        } else {
            photoView.setImageResource(R.drawable.img_medicine);
        }

        mBuilder.setView(mView);
        AlertDialog mDialog = mBuilder.create();
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

        btnClose.setOnClickListener(v -> mDialog.dismiss());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onClickDeleteMedicine(Medicine medicine, int position) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete medicine")
                .setMessage("You want to delete this medicine ?");

        alertDialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

        alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> {
            if (medicineItem.size() > position) {
                dbApp.deleteMedicine(medicine.getName());
                medicineItem.remove(position);
                medicineAdapter.notifyDataSetChanged();

                AppUtils.toast(this, "Success delete this medicine !");

                if (medicineItem == null || medicineItem.size() == 0) {
                    llNoData.setVisibility(View.VISIBLE);
                    rvTimer.setVisibility(View.GONE);
                    btnAddMedicine.setVisibility(View.GONE);
                } else {
                    llNoData.setVisibility(View.GONE);
                    rvTimer.setVisibility(View.VISIBLE);
                    btnAddMedicine.setVisibility(View.VISIBLE);
                }
            }
            dialog.dismiss();

        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
