package com.dab.medireminder.services;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;

import com.dab.medireminder.data.model.IgnoreReason;
import com.dab.medireminder.utils.AlarmUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



//For listening incoming notifications
//Setting manifests
//

@SuppressLint("OverrideAbstract")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {

    private static boolean isInitialized, isSuspended, isScreenOn;
    private AudioManager audioMan;
    private TelephonyManager telephony;
    private final DeviceStateReceiver stateReceiver = new DeviceStateReceiver();
    private final OnStatusChangeListener statusListener = () -> {
    };
    private static final List<OnStatusChangeListener> statusListeners = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //method to posted new notification
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    //method when notifications are removed
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        String appID = "com.dab.medireminder";
        if (sbn != null && sbn.getPackageName().equals(appID)) {
            AlarmUtils.getAlarmUtils().cancelAlarm(this);
        }
    }

    //Method wait for onNotificationPosted event before executed any operations
    @Override
    public IBinder onBind(Intent intent) {
        if (isInitialized) return super.onBind(intent);
        audioMan = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(stateReceiver, filter);
        registerOnStatusChangeListener(statusListener);
        setInitialized(true);
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (isInitialized) {
            unregisterReceiver(stateReceiver);
            setInitialized(false);
            unregisterOnStatusChangeListener(statusListener);
        }
        return false;
    }

    //Checking is Notification have permission?
    public static boolean isRunning() {
        return isInitialized;
    }

    private void setInitialized(boolean initialized) {
        isInitialized = initialized;
        onStatusChanged();
    }


    //Most of function below is checking phone status for example:
    //Phone still on screen, slient mode, connect to head phone bluetooth
    public static boolean isSuspended() {
        return isSuspended;
    }

    public static boolean toggleSuspend() {
        isSuspended ^= true;
        onStatusChanged();
        return isSuspended;
    }

    public static boolean toggleSuspend(boolean status) {
        isSuspended = status;
        onStatusChanged();
        return isSuspended;
    }

    private boolean isScreenOn() {
        isScreenOn = CheckScreen.isScreenOn(this);
        return isScreenOn;
    }

    private boolean isHeadsetOn() {
        return (audioMan.isBluetoothA2dpOn() || audioMan.isWiredHeadsetOn());
    }

    public static void registerOnStatusChangeListener(OnStatusChangeListener listener) {
        statusListeners.add(listener);
    }

    public static void unregisterOnStatusChangeListener(OnStatusChangeListener listener) {
        statusListeners.remove(listener);
    }

    public interface OnStatusChangeListener {
        void onStatusChanged();
    }

    private static void onStatusChanged() {
        for (OnStatusChangeListener l : statusListeners) {
            l.onStatusChanged();
        }
    }

    private static class CheckScreen {
        private static PowerManager powerMan;

        private static boolean isScreenOn(Context c) {
            if (powerMan == null) {
                powerMan = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
            }
            assert powerMan != null;
            // Prevent Lint warning. Should never be null, I want a crash report if it is.
            return powerMan.isScreenOn();
        }
    }

    private Set<IgnoreReason> ignore() {
        Set<IgnoreReason> ignoreReasons = new HashSet<>();
        if (isSuspended) {
            ignoreReasons.add(IgnoreReason.SUSPENDED);
        }
        if ((audioMan.getRingerMode() == AudioManager.RINGER_MODE_SILENT
                || audioMan.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)) {
            ignoreReasons.add(IgnoreReason.SILENT);
        }
        if (telephony.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
            ignoreReasons.add(IgnoreReason.CALL);
        }
        if (!isScreenOn()) {
            ignoreReasons.add(IgnoreReason.SCREEN_OFF);
        }
        if (isScreenOn()) {
            ignoreReasons.add(IgnoreReason.SCREEN_ON);
        }
        if (!isHeadsetOn()) {
            ignoreReasons.add(IgnoreReason.HEADSET_OFF);
        }
        if (isHeadsetOn()) {
            ignoreReasons.add(IgnoreReason.HEADSET_ON);
        }
        return ignoreReasons;
    }

    private static class DeviceStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            switch (action) {
                case Intent.ACTION_SCREEN_ON:
                    isScreenOn = true;
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    isScreenOn = false;
                    break;
            }
        }
    }
}
