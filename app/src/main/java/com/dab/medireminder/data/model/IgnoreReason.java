package com.dab.medireminder.data.model;

import android.content.Context;

import com.dab.medireminder.R;

import java.util.Iterator;
import java.util.Set;

public enum IgnoreReason {
    SUSPENDED(R.string.reason_suspended),
    SILENT(R.string.reason_silent),
    CALL(R.string.reason_call),
    SCREEN_OFF(R.string.reason_screen_off),
    SCREEN_ON(R.string.reason_screen_on),
    HEADSET_OFF(R.string.reason_headset_off),
    HEADSET_ON(R.string.reason_headset_on);

    private final int stringId;

    IgnoreReason(int resId) {
        this.stringId = resId;
    }

    String getString(Context c) {
        return c.getString(stringId);
    }

    public static String convertSetToString(Set<IgnoreReason> reasons, Context c) {
        StringBuilder builder = new StringBuilder();
        Iterator<IgnoreReason> iterator = reasons.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next().getString(c));
            if (iterator.hasNext()) builder.append(", ");
        }
        return builder.toString();
    }
}
