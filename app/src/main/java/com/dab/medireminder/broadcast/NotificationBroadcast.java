package com.dab.medireminder.broadcast;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dab.medireminder.constant.Constants;
import com.dab.medireminder.data.DBApp;
import com.dab.medireminder.data.model.MedicineTimer;
import com.dab.medireminder.utils.AlarmUtils;
import com.dab.medireminder.utils.NotificationUtils;


//Create Alarm to notify user phone to occur when the device starts or a notification is received on the device
public class NotificationBroadcast extends BroadcastReceiver {

    //Setting Database
    private Context mContext;
    private DBApp dbApp;

    //method is called when receiving an Intent broadcast.
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        dbApp = new DBApp(mContext);
        mContext = context;
        int code = intent.getIntExtra(Constants.CODE_NOTIFICATION_KEY, -1);
        String nameMedicine = intent.getStringExtra(Constants.NOTIFICATION_MEDICINE_NAME);
        String doseMedicine = intent.getStringExtra(Constants.NOTIFICATION_MEDICINE_DOSE);
        String iconMedicine = intent.getStringExtra(Constants.NOTIFICATION_MEDICINE_ICON);
        String id = intent.getStringExtra(Constants.ID_MEDICINE);
        if (code == Constants.CODE_MEDICINE_TIMER_NOTIFICATION) {
            String content = "Hello, It's time to take the medicine " + nameMedicine + " with the dose " + doseMedicine;
            NotificationUtils.getNotificationUtils(context).showNotification(iconMedicine, content);
            setUpForNextTime(id);
        }
    }

    private void setUpForNextTime(String id) {
        MedicineTimer medicineTimer = dbApp.getMedicineTimer(id);
        if (medicineTimer != null) {
            AlarmUtils.getAlarmUtils().setUpAlarmMedicine(mContext, medicineTimer);
        }
    }
}
