package com.dab.medireminder.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.dab.medireminder.R;
import com.dab.medireminder.ui.activity.MainActivity;

public class NotificationUtils {

    private final Context context;

    @SuppressLint("StaticFieldLeak")
    private static NotificationUtils NotificationUtils;

    public static NotificationUtils getNotificationUtils(Context context) {
        if (NotificationUtils == null) {
            NotificationUtils = new NotificationUtils(context);
        }
        return NotificationUtils;
    }


    public NotificationUtils(Context context) {
        this.context = context;
    }

    public void showNotification(String pathIcon, String content) {
        String title = "Medicine Reminder";
        new GetBitMap(bitmap -> setUpNotification(bitmap, title, content))
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,pathIcon);
    }

    private void setUpNotification(Bitmap bitmap, String title, String description) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_desc, description);
        if (bitmap != null) {
            Bitmap bitmapRounded = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            Canvas canvas = new Canvas(bitmapRounded);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawRoundRect((new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight())), 8, 8, paint);

            remoteViews.setImageViewBitmap(R.id.iv_icon, bitmapRounded);
        } else {
            remoteViews.setImageViewResource(R.id.iv_icon, R.drawable.logo_app);
        }

        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int notificationID = 2002;
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "medicine_reminder_id");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(getNotificationIcon())
                    .setContentText(description)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSmallIcon(R.drawable.logo_app)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContent(remoteViews)
                    .setCustomBigContentView(remoteViews)
                    .setCustomContentView(remoteViews);

        }
        Notification notification = builder.build();
        notifyManager.notify(notificationID, notification);
    }

    private int getNotificationIcon() {
        return R.drawable.logo_app;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetBitMap extends AsyncTask<String, Void, Bitmap> {
        IListenerBitMap iListenerBitMap;

        public GetBitMap(IListenerBitMap iListenerBitMap) {
            this.iListenerBitMap = iListenerBitMap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromPathFile(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            iListenerBitMap.onSuccess(result);
        }
    }

    public interface IListenerBitMap {
        void onSuccess(Bitmap bitmap);
    }

    private Bitmap getBitmapFromPathFile(String strURL) {
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            return BitmapFactory.decodeFile(strURL,bmOptions);
        } catch (Exception e) {
            return null;
        }
    }
}
