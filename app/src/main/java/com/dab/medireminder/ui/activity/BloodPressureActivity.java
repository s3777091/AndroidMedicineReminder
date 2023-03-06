package com.dab.medireminder.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;
import com.dab.medireminder.data.DBApp;
import com.dab.medireminder.data.model.BloodPressure;
import com.dab.medireminder.ui.adapter.BloodPressureAdapter;
import com.dab.medireminder.utils.AppUtils;
import com.dab.medireminder.utils.RecyclerUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class BloodPressureActivity extends BaseActivity implements BloodPressureAdapter.BloodPressureListener {

    public static final int REQUEST_ADD_BLOOD_PRESSURE = 300;
    public static final int RESULT_ADD_BLOOD_PRESSURE = 301;

    @BindView(R.id.rv_timer)
    RecyclerView rvTimer;

    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    @BindView(R.id.ll_no_data)
    View llNoData;

    @BindView(R.id.btn_add_medicine)
    FloatingActionButton btnAddBloodPressure;

    private DBApp dbApp;
    private BloodPressureAdapter bloodPressureApdater;

    private List<BloodPressure> bloodPressureItem;

    @Override
    public int getLayout() {
        return R.layout.activity_blood_pressure;
    }

    @Override
    public void initView() {
        RecyclerUtils.setupVerticalRecyclerView(this, rvTimer);
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
        bloodPressureItem = new ArrayList<>();
        bloodPressureItem = dbApp.getBloodPressure();

        if (bloodPressureItem == null || bloodPressureItem.size() == 0) {
            llNoData.setVisibility(View.VISIBLE);
            rvTimer.setVisibility(View.GONE);
            btnAddBloodPressure.setVisibility(View.GONE);
        } else {
            llNoData.setVisibility(View.GONE);
            rvTimer.setVisibility(View.VISIBLE);
            btnAddBloodPressure.setVisibility(View.VISIBLE);
        }

        Collections.sort(bloodPressureItem, (o1, o2) -> {
            if (o1.getTimer() > o2.getTimer()) {
                return -1;
            } else if (o1.getTimer() < o2.getTimer()) {
                return 1;
            }
            return 0;
        });

        bloodPressureApdater = new BloodPressureAdapter(this, bloodPressureItem, this);
        rvTimer.setAdapter(bloodPressureApdater);
        pbLoading.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_close)
    public void closeScreen() {
        finish();
    }


    @OnClick({R.id.btn_add_blood, R.id.btn_add_medicine})
    public void addNewBloodPressure() {
        startActivityForResult(new Intent(this, AddBloodPressureActivity.class), REQUEST_ADD_BLOOD_PRESSURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_BLOOD_PRESSURE && resultCode == RESULT_ADD_BLOOD_PRESSURE) {
            loadData();
        }
    }

    @Override
    public void onClickBloodPressure(BloodPressure bloodPressure) {

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onClickDeleteBloodPressure(BloodPressure bloodPressure, int position) {
        if (bloodPressureItem.size() > position) {
            dbApp.deleteBloodPressure(String.valueOf(bloodPressure.getTimer()));
            bloodPressureItem.remove(position);
            bloodPressureApdater.notifyDataSetChanged();

            AppUtils.toast(this, "Success delete this Blood Pressure !");

            if (bloodPressureItem == null || bloodPressureItem.size() == 0) {
                llNoData.setVisibility(View.VISIBLE);
                rvTimer.setVisibility(View.GONE);
                btnAddBloodPressure.setVisibility(View.GONE);
            } else {
                llNoData.setVisibility(View.GONE);
                rvTimer.setVisibility(View.VISIBLE);
                btnAddBloodPressure.setVisibility(View.VISIBLE);
            }

        }
    }
}
