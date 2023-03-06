package com.dab.medireminder.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.core.content.ContextCompat;

import com.dab.medireminder.R;
import com.dab.medireminder.data.model.BloodPressure;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChartUtils {

    private static final float sizeText = 14f;

    //Create chart format
    @SuppressLint("DefaultLocale")
    public static void initBarChart(Context context, BarChart barChart, List<BloodPressure> data) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-SemiBold.ttf");
        barChart.invalidate();
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setClickable(true);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setPadding(0, 0, 0, 0);
        barChart.setGridBackgroundColor(Color.parseColor("#FE9A00"));
        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(30);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.setNoDataText("Missing Data");
        barChart.setExtraBottomOffset(10f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);
        xAxis.setValueFormatter(((value, axis) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(data.get((int) value - 1).getTimer());
            return String.format("%02d/%02d/%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        }));
        xAxis.setTextSize(10f);
        xAxis.setTypeface(typeface);
        xAxis.setTextColor(context.getResources().getColor(R.color.color_text));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(((value, axis) -> {
            DecimalFormat mFormat = new DecimalFormat("###,###,###,###");
            return "" + mFormat.format(value).replace(",", ".");
        }));
        leftAxis.setTextColor(context.getResources().getColor(R.color.color_text));
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setGridColor(context.getResources().getColor(R.color.color_ececec));
        leftAxis.setAxisLineColor(context.getResources().getColor(R.color.color_b7b7b7));
        leftAxis.setAxisLineWidth(0.5f);
        leftAxis.setGridLineWidth(2f);
        leftAxis.setTextSize(10f);
        leftAxis.setTypeface(typeface);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        Legend l = barChart.getLegend();
        l.setEnabled(false);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(4f);
        l.setTextSize(sizeText);
        l.setTypeface(typeface);
        l.setXEntrySpace(4f);

        ArrayList<BarEntry> values = new ArrayList<>();
        float start = 1f;
        for (int i = (int) start; i < start + data.size(); i++) {
            float delta = (float) (data.get(i - 1).getMax() + data.get(i - 1).getMin()) / (float) 2;
            values.add(new BarEntry(i, delta, data.get(i-1)));
        }

        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);

            set1.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setDrawValues(true);
            int endColor = ContextCompat.getColor(context, R.color.color_chart_prepay);
            set1.setColors(endColor);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData barData = new BarData(dataSets);
            barData.setValueTextSize(sizeText);
            barData.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> {
                if (entry.getData() instanceof BloodPressure) {
                    BloodPressure bloodPressure = (BloodPressure) entry.getData();
                    return bloodPressure.getMax() + "/" + bloodPressure.getMin();
                }
                return formatDecimal(BigDecimal.valueOf(value));
            });
            barData.setValueTextColor(context.getResources().getColor(R.color.blue_1));
            barData.setValueTypeface(typeface);
            barData.setBarWidth(0.1f);
            barChart.setData(barData);
            barData.setHighlightEnabled(false);

        }
    }

    private static String formatDecimal(BigDecimal value) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###.#;#,###.#-", otherSymbols);
        return df.format(value);
    }
}
