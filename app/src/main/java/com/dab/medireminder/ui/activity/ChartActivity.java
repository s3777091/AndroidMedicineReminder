package com.dab.medireminder.ui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;
import com.dab.medireminder.data.DBApp;
import com.dab.medireminder.data.model.BloodPressure;
import com.dab.medireminder.utils.ChartUtils;
import com.github.mikephil.charting.charts.BarChart;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ChartActivity extends BaseActivity {

    @BindView(R.id.bar_chart)
    BarChart barChart;

    @BindView(R.id.tv_no_data)
    TextView tvNoData;

    private DBApp dbApp;

    @Override
    public int getLayout() {
        return R.layout.activity_chart;
    }

    @Override
    public void initView() {
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
        List<BloodPressure> bloodPressureItem = dbApp.getBloodPressure();

        if (bloodPressureItem.size() <= 0) {
            tvNoData.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
        }

        Collections.sort(bloodPressureItem, (o1, o2) -> {
            if (o1.getTimer() > o2.getTimer()) {
                return 1;
            } else if (o1.getTimer() < o2.getTimer()) {
                return -1;
            }
            return 0;
        });

        ChartUtils.initBarChart(this, barChart, bloodPressureItem);
    }

    @OnClick(R.id.btn_close)
    public void closeScreen() {
        finish();
    }
}
