package com.dab.medireminder.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.dab.medireminder.R;


//Create Need Text components have google styles
public class UITextViewCustom extends AppCompatTextView {

    public UITextViewCustom(Context context) {
        super(context);
    }

    public UITextViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public UITextViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.UITextViewCustom);
        String customFont = a.getString(R.styleable.UITextViewCustom_setStyleTextView);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public void setCustomFont(Context ctx, String customFont) {
        Typeface typeface;
        String fontName = "OpenSans-Regular.ttf";
        try {
            if (customFont != null) {
                if (customFont.equals(ctx.getString(R.string.open_sans_regular))) {
                    fontName = "OpenSans-Regular.ttf";
                } else if (customFont.equals(ctx.getString(R.string.open_sans_bold))) {
                    fontName = "OpenSans-Bold.ttf";
                } else if (customFont.equals(ctx.getString(R.string.open_sans_semi_bold))) {
                    fontName = "OpenSans-SemiBold.ttf";
                } else if (customFont.equals(ctx.getString(R.string.open_sans_italic))) {
                    fontName = "OpenSans-Italic.ttf";
                } else {
                    fontName = "OpenSans-Regular.ttf";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + fontName);
        setTypeface(typeface);
    }
}
