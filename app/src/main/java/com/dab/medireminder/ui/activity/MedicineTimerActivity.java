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
import com.dab.medireminder.data.model.MedicineTimer;
import com.dab.medireminder.ui.adapter.MedicineTimerAdapter;
import com.dab.medireminder.utils.AppUtils;
import com.dab.medireminder.utils.RecyclerUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


@SuppressLint("NonConstantResourceId")
public class MedicineTimerActivity extends BaseActivity implements MedicineTimerAdapter.MedicineTimerListener {

    public static final int REQUEST_ADD_MEDICINE_TIMER = 100;
    public static final int RESULT_ADD_MEDICINE_TIMER = 101;

    @BindView(R.id.rv_timer)
    RecyclerView rvTimer;

    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    @BindView(R.id.ll_no_data)
    View llNoData;

    @BindView(R.id.btn_add_medicine)
    FloatingActionButton btnAddMedicine;

    private DBApp dbApp;
    private MedicineTimerAdapter medicineTimerAdapter;

    private List<MedicineTimer> timerList;

    @Override
    public int getLayout() {
        return R.layout.activity_medicine_timer;
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
        timerList = new ArrayList<>();
        timerList = dbApp.getMedicineTimer();

        if (timerList == null || timerList.size() == 0) {
            llNoData.setVisibility(View.VISIBLE);
            rvTimer.setVisibility(View.GONE);
            btnAddMedicine.setVisibility(View.GONE);
        } else {
            llNoData.setVisibility(View.GONE);
            rvTimer.setVisibility(View.VISIBLE);
            btnAddMedicine.setVisibility(View.VISIBLE);
        }

        medicineTimerAdapter = new MedicineTimerAdapter(this, timerList, this);
        rvTimer.setAdapter(medicineTimerAdapter);
        pbLoading.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_close)
    public void closeScreen() {
        finish();
    }

    @OnClick({R.id.btn_add_medicine, R.id.btn_timer_new})
    public void addNewMedicine() {
        startActivityForResult(new Intent(this, AddMedicineTimerActivity.class), REQUEST_ADD_MEDICINE_TIMER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_MEDICINE_TIMER && resultCode == RESULT_ADD_MEDICINE_TIMER) {
            loadData();
        }
    }

    @Override
    public void onClickMedicineTimer(MedicineTimer medicineTimer) {

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onClickDeleteTimer(MedicineTimer medicineTimer, int position) {
        if (timerList.size() > position) {
            dbApp.deleteTimer(medicineTimer.getId());
            timerList.remove(position);
            medicineTimerAdapter.notifyDataSetChanged();

            AppUtils.toast(this, "Canceled medication appointment !");

            if (timerList == null || timerList.size() == 0) {
                llNoData.setVisibility(View.VISIBLE);
                rvTimer.setVisibility(View.GONE);
                btnAddMedicine.setVisibility(View.GONE);
            } else {
                llNoData.setVisibility(View.GONE);
                rvTimer.setVisibility(View.VISIBLE);
                btnAddMedicine.setVisibility(View.VISIBLE);
            }

        }
    }
}
