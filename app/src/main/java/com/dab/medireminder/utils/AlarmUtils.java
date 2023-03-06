package com.dab.medireminder.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dab.medireminder.broadcast.NotificationBroadcast;
import com.dab.medireminder.constant.Constants;
import com.dab.medireminder.data.model.MedicineTimer;

import java.util.Calendar;
import java.util.List;

public class AlarmUtils {

    public static AlarmUtils alarmUtils;

    public static AlarmUtils getAlarmUtils() {
        if (alarmUtils == null) {
            alarmUtils = new AlarmUtils();
        }
        return alarmUtils;
    }

    public void setUpAlarmMedicine(Context context, MedicineTimer medicineTimer) {
        try {
            if (medicineTimer != null) {
                Calendar calendar = Calendar.getInstance();
                Integer dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                List<Integer> integers = medicineTimer.getDayOfWeek();

                if (integers.contains(dayOfWeek)) {
                    setTimer(context, 0, medicineTimer);
                } else {
                    for (Integer integer : integers) {
                        if (dayOfWeek == 7 && integer == 1) {
                            setTimer(context, 1, medicineTimer);
                            break;
                        } else if (integer > dayOfWeek) {
                            setTimer(context, Math.abs(integer - dayOfWeek), medicineTimer);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("AlarmUtils", "setUpAlarmMedicine: " + e);
        }
    }

    public void setTimer(Context context, int day, MedicineTimer medicineTimer) {
        String timer = medicineTimer.getTimer();

        if (timer.contains(",")) {
            String[] listTime = timer.split(",");
            for (String s : listTime) {
                String[] hourTime = s.split(":");
                int h = Integer.parseInt(hourTime[0].trim());
                int m = Integer.parseInt(hourTime[1].trim());

                setAlarm(context, day, medicineTimer, h, m);
            }
        } else {
            String[] hourTime = timer.split(":");
            int h = Integer.parseInt(hourTime[0].trim());
            int m = Integer.parseInt(hourTime[1].trim());

            setAlarm(context, day, medicineTimer, h, m);
        }
    }

    @SuppressLint("ShortAlarm")
    public void setAlarm(Context context, int day, MedicineTimer medicineTimer, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, NotificationBroadcast.class);
        intent.putExtra(Constants.CODE_NOTIFICATION_KEY, Constants.CODE_MEDICINE_TIMER_NOTIFICATION);
        intent.putExtra(Constants.NOTIFICATION_MEDICINE_DOSE, medicineTimer.getDose());
        intent.putExtra(Constants.NOTIFICATION_MEDICINE_NAME, medicineTimer.getName());
        intent.putExtra(Constants.NOTIFICATION_MEDICINE_ICON, medicineTimer.getIcon());
        intent.putExtra(Constants.ID_MEDICINE, medicineTimer.getId());
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5 * 1000, pendingIntent);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, NotificationBroadcast.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
