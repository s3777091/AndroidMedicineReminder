package com.dab.medireminder.ui.activity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dab.medireminder.R;
import com.dab.medireminder.base.BaseActivity;
import com.dab.medireminder.data.DBApp;
import com.dab.medireminder.data.model.HourTimer;
import com.dab.medireminder.data.model.Medicine;
import com.dab.medireminder.data.model.MedicineTimer;
import com.dab.medireminder.services.NotificationListener;
import com.dab.medireminder.ui.adapter.HourTimerAdapter;
import com.dab.medireminder.ui.adapter.SuggestMedicineAdapter;
import com.dab.medireminder.ui.fragment.PickerTimerFragment;
import com.dab.medireminder.utils.AlarmUtils;
import com.dab.medireminder.utils.AppUtils;
import com.dab.medireminder.utils.RecyclerUtils;
import com.dab.medireminder.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.dab.medireminder.ui.activity.MedicineTimerActivity.RESULT_ADD_MEDICINE_TIMER;

@SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged", "SetTextI18n"})
public class AddMedicineTimerActivity extends BaseActivity implements HourTimerAdapter.HourTimerListener,
        PickerTimerFragment.PickerUiListener, SuggestMedicineAdapter.MedicineListener {

    @BindView(R.id.edt_name)
    EditText edtName;

    @BindView(R.id.edit_dose)
    EditText edtDose;

    @BindView(R.id.rv_timer)
    RecyclerView rvTimer;

    @BindView(R.id.tv_repeat)
    TextView tvRepeat;

    @BindView(R.id.rl_main)
    View rlMain;

    @BindView(R.id.ll_suggest)
    View llSuggest;

    @BindView(R.id.rcv_suggest)
    RecyclerView rcvSuggest;

    private DBApp dbApp;
    private MedicineTimer medicineTimer;
    private HourTimerAdapter hourTimerAdapter;
    private SuggestMedicineAdapter suggestMedicineAdapter;

    private List<HourTimer> hourTimerList;
    private List<Integer> listDayOfWeek;
    private List<Medicine> listSuggest;
    private List<Medicine> listMedicine;

    private PickerTimerFragment pickerTimerFragment;
    private Medicine medicineCurrent;

    private final NotificationListener.OnStatusChangeListener statusListener = this::updateStatus;

    @Override
    public void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationListener.registerOnStatusChangeListener(statusListener);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationListener.unregisterOnStatusChangeListener(statusListener);
        }
        super.onPause();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_add_medicine_timer;
    }

    @Override
    public void initView() {
        RecyclerUtils.setupHorizontalRecyclerView(this, rvTimer);
        RecyclerUtils.setupVerticalRecyclerView(this, rcvSuggest);
        hideKeyboard(rlMain);
    }

    @Override
    public void setEvents() {
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
                listSuggest.clear();
                String nameSearch = edtName.getText().toString().trim();
                if ("".equals(nameSearch)) {
                    llSuggest.setVisibility(View.GONE);
                } else {
                    for (Medicine medicine : listMedicine) {
                        if (medicine.getName().toLowerCase().contains(nameSearch.toLowerCase())) {
                            listSuggest.add(medicine);
                        }
                    }
                    if (listSuggest.size() > 0) {
                        if (listSuggest.size() <= 4) {
                            rcvSuggest.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                        } else {
                            rcvSuggest.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewUtils.convertDpToPx(255, AddMedicineTimerActivity.this)));
                        }
                        llSuggest.setVisibility(View.VISIBLE);
                        if (suggestMedicineAdapter != null) {
                            suggestMedicineAdapter.notifyDataSetChanged();
                        }
                    } else {
                        llSuggest.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void setData() {
        dbApp = new DBApp(this);

        tvRepeat.setText("One time Reminder");
        listDayOfWeek = new ArrayList<>();
        hourTimerList = new ArrayList<>();
        listSuggest = new ArrayList<>();
        listMedicine = new ArrayList<>();
        hourTimerList.add(new HourTimer(true));
        hourTimerAdapter = new HourTimerAdapter(this, hourTimerList, this);
        rvTimer.setAdapter(hourTimerAdapter);

        Calendar calendar = Calendar.getInstance();

        pickerTimerFragment = new PickerTimerFragment(
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
        );

        listMedicine = dbApp.getMedicine();
        suggestMedicineAdapter = new SuggestMedicineAdapter(this, listSuggest, this);
        rcvSuggest.setAdapter(suggestMedicineAdapter);
    }

    private void addTimer() {
        String name = edtName.getText().toString();
        String dose = edtDose.getText().toString();
        String repeat = tvRepeat.getText().toString();
        StringBuilder timerBuilder = new StringBuilder();

        for (int i = 0; i < hourTimerList.size(); i++) {
            HourTimer hourTimer = hourTimerList.get(i);
            if (!TextUtils.isEmpty(hourTimer.getName())) {
                timerBuilder.append(hourTimer.getName());
            }

            if (i < hourTimerList.size() - 2) {
                timerBuilder.append(", ");
            }
        }

        if (TextUtils.isEmpty(name)) {
            AppUtils.toast(this, "Need input name medicine !");
            return;
        }

        if (TextUtils.isEmpty(dose)) {
            AppUtils.toast(this, "Need input dose !");
            return;
        }

        if (TextUtils.isEmpty(timerBuilder.toString())) {
            AppUtils.toast(this, "Need input time !");
            return;
        }

        if (medicineTimer == null) {
            medicineTimer = new MedicineTimer();
        }

        medicineTimer.setId(name + System.currentTimeMillis());
        medicineTimer.setName(name);
        medicineTimer.setDose(dose);
        medicineTimer.setRepeat(repeat);
        medicineTimer.setTimer(timerBuilder.toString());

        if (medicineCurrent == null) {
            medicineCurrent = dbApp.getMedicine(name);
        }

        if (medicineCurrent != null) {
            medicineTimer.setIcon(medicineCurrent.getImage());
        }

        if (!dbApp.addTimer(medicineTimer)) {
            AppUtils.toast(this, "Oh ! I can't add Medicine !");
            edtName.requestFocus();
        } else {
            AlarmUtils.getAlarmUtils().setUpAlarmMedicine(this, medicineTimer);
            AppUtils.toast(this, "Success add medicine time !");
            setResult(RESULT_ADD_MEDICINE_TIMER);
            finish();
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_timer, R.id.tv_repeat})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                setResult(RESULT_ADD_MEDICINE_TIMER);
                finish();
                break;
            case R.id.btn_timer:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && !NotificationListener.isRunning()) {
                    accessNotification();
                } else {
                    addTimer();
                }
                break;
            case R.id.tv_repeat:
                setUpRepeat();
                break;
        }
    }


    @Override
    public void onClickAddHourTimer(HourTimer medicineTimer) {
        Calendar calendar = Calendar.getInstance();
        if (pickerTimerFragment != null && !pickerTimerFragment.isShow) {
            pickerTimerFragment.setData(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            pickerTimerFragment.show(getSupportFragmentManager(), pickerTimerFragment.getTag());
        }
    }

    @Override
    public void onClickDeleteTimer(HourTimer medicineTimer, int position) {
        if (hourTimerList.size() > position) {
            hourTimerList.remove(position);
            hourTimerAdapter.notifyDataSetChanged();
        }
    }

    //Click choice day of week
    private void setUpRepeat() {
        CharSequence[] items = new CharSequence[]{
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        };
        boolean[] itemsSelected = new boolean[]{
                false,
                false,
                false,
                false,
                false,
                false,
                false
        };
        for (Integer integer : listDayOfWeek) {
            itemsSelected[integer] = true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog pageDialog = builder
                .setTitle("Repeat")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", (dialogInterface, i) -> setValueRepeat(itemsSelected))
                .setMultiChoiceItems(items, itemsSelected, (dialogInterface, i, b) -> itemsSelected[i] = b)
                .create();
        pageDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private String StringDays(int value) {
        switch (value) {
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            case 8:
                return "Sunday";
            default:
                return "Apocalypse";
        }
    }


    //Control user selected
    private void setValueRepeat(boolean[] itemsSelected) {
        boolean isAll = true;
        listDayOfWeek = new ArrayList<>();
        for (int i = 0; i < itemsSelected.length; i++) {
            if (!itemsSelected[i]) {
                isAll = false;
            } else {
                listDayOfWeek.add(i);
            }
        }

        if (listDayOfWeek.size() > 0) {
            if (listDayOfWeek.size() == 1) {
                int value = listDayOfWeek.get(0) + 2;
                tvRepeat.setText(StringDays(value));
            } else {
                Collections.sort(listDayOfWeek, (o1, o2) -> {
                    if (o1 < o2) {
                        return -1;
                    } else if (o1 > o2) {
                        return 1;
                    }
                    return 0;
                });

                int max = listDayOfWeek.get(listDayOfWeek.size() - 1);
                for (int j = listDayOfWeek.size() - 1; j >= 0; j--) {
                    if (listDayOfWeek.get(j) == max) {
                        continue;
                    }

                    if (listDayOfWeek.get(j) + 1 == max) {
                        max = listDayOfWeek.get(j);
                    }
                }

                if (max == listDayOfWeek.get(0)) {
                    int value1 = listDayOfWeek.get(0) + 2;
                    int value2 = listDayOfWeek.get(listDayOfWeek.size() - 1) + 2;
                    if (value2 == 8) {
                        tvRepeat.setText(StringDays(value1).concat(" to ").concat("Sunday"));
                    } else {
                        tvRepeat.setText(StringDays(value1).concat(" to ").concat(StringDays(value2)));
                    }
                } else {
                    StringBuilder builder = new StringBuilder();
                    for (Integer integer : listDayOfWeek) {
                        int value = integer + 2;
                        builder.append(StringDays(value));
                        if (!listDayOfWeek.get(listDayOfWeek.size() - 1).equals(integer)) {
                            builder.append(", ");
                        }
                    }
                    tvRepeat.setText(builder.toString());
                }
            }
        } else {
            tvRepeat.setText("One-time reminder");
        }

        if (isAll) {
            tvRepeat.setText("Daily");
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onChooseClick(int hour, int minute, int positionHour, int positionMinute) {
        for (HourTimer hourTimer : hourTimerList) {
            if (hourTimer.getHour() == hour && hourTimer.getMinute() == minute) {
                AppUtils.toast(this, "Already have this time !");
                return;
            }
        }

        HourTimer hourTimer = new HourTimer();
        hourTimer.setHour(hour);
        hourTimer.setMinute(minute);
        hourTimer.setPositionHour(positionHour);
        hourTimer.setPositionMinute(positionMinute);
        hourTimer.setName(String.format("%02d:%02d", hour, minute));

        hourTimerList.add(0, hourTimer);
        hourTimerAdapter.notifyDataSetChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void hideKeyboard(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                AppUtils.hideSoftKeyboard(v, AddMedicineTimerActivity.this);
                return false;
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboard(innerView);
            }
        }
    }

    @Override
    public void onClickMedicine(Medicine medicine) {
        medicineCurrent = medicine;
        edtName.setText(medicine.getName());
        edtDose.setText(medicine.getDose());
        listSuggest.clear();
        if (suggestMedicineAdapter != null) {
            suggestMedicineAdapter.notifyDataSetChanged();
        }
        llSuggest.setVisibility(View.GONE);
        edtName.setSelection(medicine.getName().length());
    }

    //Need Notification Permission for alarm
    private void accessNotification() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Receive medication reminders")
                .setMessage("To receive a reminder to take your medicine on time, do you turn ON access to the app's Alarm?");

        alertDialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

        alertDialogBuilder.setPositiveButton("OK", (dialog, id) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build();
                    Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chisten);
                    CharSequence name = getString(R.string.app_name);
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("medicine_reminder_id", name, importance);
                    channel.enableVibration(true);
                    channel.setVibrationPattern(new long[]{1000, 2000});
                    channel.enableLights(true);
                    channel.setLightColor(Color.GREEN);
                    channel.setSound(soundUri, audioAttributes);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationListener.toggleSuspend();
            }
            dialog.dismiss();
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //If Can't get notification
    private void updateStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (NotificationListener.isSuspended() && NotificationListener.isRunning()) {
                addTimer();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                } else {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            }
        } else {
            addTimer();
        }
    }
}
