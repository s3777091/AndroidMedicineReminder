package com.dab.medireminder.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ViewUtils {

    public static int convertDpToPixel(float dp, Context context) {
        if (context != null) {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            return (int) px;
        }
        return 0;
    }

    public static int convertDpToPx(float dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
