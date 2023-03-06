package com.dab.medireminder.dialog.picker;

import android.os.Parcel;
import android.os.Parcelable;

import com.dab.medireminder.R;
import com.dab.medireminder.dialog.picker.blur.PickerUIBlur;

import java.util.List;

/**
 *
 */

public class PickerUISettings implements Parcelable {

    public static final Creator<PickerUISettings> CREATOR;

    static {
        CREATOR = new Creator<PickerUISettings>() {
            public PickerUISettings createFromParcel(Parcel source) {
                return new PickerUISettings(source);
            }

            public PickerUISettings[] newArray(int size) {
                return new PickerUISettings[size];
            }
        };
    }

    /**
     * Default behaviour of PickerUi when an item is selected
     */
    public static final boolean DEFAULT_AUTO_DISMISS = true;
    /**
     * Default behaviour of items
     */
    public static final boolean DEFAULT_ITEMS_CLICKABLES = true;
    private List<String> mItems;
    private List<String> mItemsMinute;
    private int mColorTextCenter;
    private int mColorTextNoCenter;
    private int mBackgroundColor;
    private int mLinesColor;
    private boolean mAutoDismiss;
    private boolean mItemsClickables;
    private boolean mItemsClickablesMinute;
    private float mBlurDownScaleFactor;
    private int mBlurRadius;
    private int mBlurFilterColor;
    private boolean mUseBlur;
    private boolean mUseBlurRenderscript;

    private PickerUISettings(Builder builder) {
        setItems(builder.mItems);
        setItemsMinute(builder.mItemsMinute);
        setColorTextCenter(builder.mColorTextCenter);
        setColorTextNoCenter(builder.mColorTextNoCenter);
        setBackgroundColor(builder.mBackgroundColor);
        setLinesColor(builder.mLinesColor);
        setItemsClickables(builder.mItemsClickables);
        setItemsClickablesMinute(builder.mItemsClickables);
        setAutoDismiss(builder.mAutoDismiss);
        setUseBlur(builder.mUseBlur);
        setUseBlurRenderscript(builder.mUseBlurRenderscript);
        setBlurDownScaleFactor(builder.mDownScaleFactor);
        setBlurRadius(builder.mRadius);
        setBlurFilterColor(builder.mFilterColor);
    }

    private PickerUISettings(Parcel in) {
        in.readStringList(this.mItems);
        in.readStringList(this.mItemsMinute);
        this.mColorTextCenter = in.readInt();
        this.mColorTextNoCenter = in.readInt();
        this.mBackgroundColor = in.readInt();
        this.mLinesColor = in.readInt();
        this.mAutoDismiss = in.readByte() != 0;
        this.mItemsClickables = in.readByte() != 0;
        this.mItemsClickablesMinute = in.readByte() != 0;
        this.mBlurDownScaleFactor = in.readFloat();
        this.mBlurRadius = in.readInt();
        this.mBlurFilterColor = in.readInt();
        this.mUseBlur = in.readByte() != 0;
        this.mUseBlurRenderscript = in.readByte() != 0;
    }

    public List<String> getItems() {
        return mItems;
    }

    void setItems(List<String> items) {
        mItems = items;
    }

    public List<String> getItemsMinute() {
        return mItemsMinute;
    }

    void setItemsMinute(List<String> items) {
        mItemsMinute = items;
    }

    public int getColorTextCenter() {
        return mColorTextCenter;
    }

    void setColorTextCenter(int colorTextCenter) {
        mColorTextCenter = colorTextCenter;
    }

    public int getColorTextNoCenter() {
        return mColorTextNoCenter;
    }

    void setColorTextNoCenter(int colorTextNoCenter) {
        mColorTextNoCenter = colorTextNoCenter;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public int getLinesColor() {
        return mLinesColor;
    }

    void setLinesColor(int linesColor) {
        mLinesColor = linesColor;
    }

    public boolean areItemsClickables() {
        return mItemsClickables;
    }


    public boolean areItemsClickablesMinute() {
        return mItemsClickablesMinute;
    }

    void setItemsClickables(boolean itemsClickables) {
        mItemsClickables = itemsClickables;
    }

    void setItemsClickablesMinute(boolean itemsClickables) {
        mItemsClickablesMinute = itemsClickables;
    }

    public boolean isAutoDismiss() {
        return mAutoDismiss;
    }

    void setAutoDismiss(boolean autoDismiss) {
        mAutoDismiss = autoDismiss;
    }

    public boolean isUseBlur() {
        return mUseBlur;
    }

    void setUseBlur(boolean useBlur) {
        mUseBlur = useBlur;
    }

    public boolean isUseBlurRenderscript() {
        return mUseBlurRenderscript;
    }

    void setUseBlurRenderscript(boolean useBlurRenderscript) {
        mUseBlurRenderscript = useBlurRenderscript;
    }

    public float getBlurDownScaleFactor() {
        return mBlurDownScaleFactor;
    }

    void setBlurDownScaleFactor(float blurDownScaleFactor) {
        mBlurDownScaleFactor = blurDownScaleFactor;
    }

    public int getBlurRadius() {
        return mBlurRadius;
    }

    void setBlurRadius(int blurRadius) {
        mBlurRadius = blurRadius;
    }

    public int getBlurFilterColor() {
        return mBlurFilterColor;
    }

    void setBlurFilterColor(int blurFilterColor) {
        mBlurFilterColor = blurFilterColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.mItems);
        dest.writeList(this.mItemsMinute);
        dest.writeInt(this.mColorTextCenter);
        dest.writeInt(this.mColorTextNoCenter);
        dest.writeInt(this.mBackgroundColor);
        dest.writeInt(this.mLinesColor);
        dest.writeByte(mAutoDismiss ? (byte) 1 : (byte) 0);
        dest.writeByte(mItemsClickables ? (byte) 1 : (byte) 0);
        dest.writeByte(mItemsClickablesMinute ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.mBlurDownScaleFactor);
        dest.writeInt(this.mBlurRadius);
        dest.writeInt(this.mBlurFilterColor);
        dest.writeByte(mUseBlur ? (byte) 1 : (byte) 0);
        dest.writeByte(mUseBlurRenderscript ? (byte) 1 : (byte) 0);
    }

    public static final class Builder {

        private List<String> mItems;
        private List<String> mItemsMinute;
        private int mColorTextCenter = R.color.text_center_pickerui;
        private int mColorTextNoCenter = R.color.text_no_center_pickerui;
        private int mBackgroundColor = R.color.background_panel_pickerui;
        private int mLinesColor = R.color.lines_panel_pickerui;
        private boolean mUseBlur = PickerUIBlur.DEFAULT_USE_BLUR;
        private boolean mUseBlurRenderscript = PickerUIBlur.DEFAULT_USE_BLUR_RENDERSCRIPT;
        private boolean mItemsClickables = DEFAULT_ITEMS_CLICKABLES;
        private boolean mItemsClickablesMinute = DEFAULT_ITEMS_CLICKABLES;
        private float mDownScaleFactor = PickerUIBlur.DEFAULT_DOWNSCALE_FACTOR;
        private int mRadius = PickerUIBlur.DEFAULT_BLUR_RADIUS;
        private boolean mAutoDismiss = DEFAULT_AUTO_DISMISS;
        private int mFilterColor = -1;

        public Builder() {
        }

        private Builder(Builder builder) {
            mUseBlurRenderscript = builder.mUseBlurRenderscript;
            mUseBlur = builder.mUseBlur;
        }

        public Builder withItems(List<String> mItems) {
            this.mItems = mItems;
            return this;
        }

        public Builder withItemsMinute(List<String> mItems) {
            this.mItemsMinute = mItems;
            return this;
        }

        public Builder withColorTextCenter(int mColorTextCenter) {
            this.mColorTextCenter = mColorTextCenter;
            return this;
        }

        public Builder withColorTextNoCenter(int mColorTextNoCenter) {
            this.mColorTextNoCenter = mColorTextNoCenter;
            return this;
        }

        public Builder withBackgroundColor(int mBackgroundColor) {
            this.mBackgroundColor = mBackgroundColor;
            return this;
        }

        public Builder withLinesColor(int mLinesColor) {
            this.mLinesColor = mLinesColor;
            return this;
        }

        public Builder withItemsClickables(boolean mItemsClickables) {
            this.mItemsClickables = mItemsClickables;
            return this;
        }

        public Builder withItemsClickablesMinute(boolean mItemsClickables) {
            this.mItemsClickablesMinute = mItemsClickables;
            return this;
        }

        public Builder withAutoDismiss(boolean mAutoDismiss) {
            this.mAutoDismiss = mAutoDismiss;
            return this;
        }

        public Builder withBlurDownScaleFactor(float mDownScaleFactor) {
            this.mDownScaleFactor = mDownScaleFactor;
            return this;
        }

        public Builder withBlurRadius(int mRadius) {
            this.mRadius = mRadius;
            return this;
        }

        public Builder withBlurFilterColor(int mFilterColor) {
            this.mFilterColor = mFilterColor;
            return this;
        }

        public Builder withUseBlurRenderscript(boolean mUseBlurRenderscript) {
            this.mUseBlurRenderscript = mUseBlurRenderscript;
            return this;
        }

        public Builder withUseBlur(boolean mUseBlur) {
            this.mUseBlur = mUseBlur;
            return this;
        }

        public PickerUISettings build() {
            return new PickerUISettings(this);
        }

    }
}