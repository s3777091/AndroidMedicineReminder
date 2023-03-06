package com.dab.medireminder.dialog.picker.blur;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dab.medireminder.R;

public class PickerUIBlurHelper {

    private static final String LOG_TAG = PickerUIBlurHelper.class.getSimpleName();

    /**
     * Down scale factor to reduce blurring time and memory allocation.
     */
    private float mDownScaleFactor = PickerUIBlur.DEFAULT_DOWNSCALE_FACTOR;

    /**
     * An imageview to display the blurred snapshot/bitmap
     */
    private ImageView mBlurredImageView;

    /**
     * Blur radius used for the background.
     */
    private int mBlurRadius = PickerUIBlur.DEFAULT_BLUR_RADIUS;

    private ViewGroup mRootView;
    private Context mContext;
    private int mFilterColor = -1;
    private int mAlpha = PickerUIBlur.CONSTANT_DEFAULT_ALPHA;
    private boolean mUseBlur = PickerUIBlur.DEFAULT_USE_BLUR;
    private boolean mUseRenderScript = PickerUIBlur.DEFAULT_USE_BLUR_RENDERSCRIPT;
    private BlurFinishedListener mBlurFinishedListener;

    /**
     * Default constructor
     */
    public PickerUIBlurHelper(Context context, AttributeSet attrs) {
        mContext = context;
        getAttributes(attrs);
        createImageViewBlur();
    }

    public PickerUIBlurHelper() {

    }

    /**
     * Generate a bitmap from a particular view.
     *
     * @param view the view to convert to a Bitmap
     * @return the bitmap of the view
     */
    public Bitmap loadBitmapFromView(View view) {
        if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
            Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            view.draw(c);

            return b;
        }
        return null;
    }


    /**
     * Downscale bitmap from view to reduce blurring time and memory allocation, using the downscale
     * factor.
     *
     * @param bitmap The Bitmap to downscale
     * @return the bitmap downscaled
     */
    public Bitmap downscaleBitmap(Bitmap bitmap) {
        int width = (int) (bitmap.getWidth() / mDownScaleFactor);
        int height = (int) (bitmap.getHeight() / mDownScaleFactor);
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    /**
     * Retrieve styles attributes
     */
    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.PickerUI, 0, 0);

        try {
            mUseBlur = typedArray.getBoolean(R.styleable.PickerUI_blur,
                    PickerUIBlur.DEFAULT_USE_BLUR);
            mBlurRadius = typedArray.getInteger(R.styleable.PickerUI_blur_radius,
                    PickerUIBlur.DEFAULT_BLUR_RADIUS);
            mDownScaleFactor = typedArray.getFloat(R.styleable.PickerUI_blur_downScaleFactor,
                    PickerUIBlur.DEFAULT_DOWNSCALE_FACTOR);
            mFilterColor = typedArray.getColor(R.styleable.PickerUI_blur_FilterColor, -1);
            mUseRenderScript = typedArray.getBoolean(R.styleable.PickerUI_blur_use_renderscript,
                    PickerUIBlur.DEFAULT_USE_BLUR_RENDERSCRIPT);

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error while creating the view PickerUI with PickerUIBlurHelper: ",
                    e);
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Apply custom down scale factor
     * <p>
     * By default down scale factor is set to {@link PickerUIBlur#MIN_DOWNSCALE}
     *
     * @param downScaleFactor Factor customized down scale factor, must be at least 1
     */
    public void setDownScaleFactor(float downScaleFactor) {
        if (!PickerUIBlur.isValidDownscale(downScaleFactor)) {
            throw new IllegalArgumentException("Invalid downsampling");
        }
        mDownScaleFactor = Math.max(downScaleFactor, PickerUIBlur.MIN_DOWNSCALE);
    }

    /**
     * Method to set the use of blur effect
     *
     * @param useBlur if want to use blur
     */
    public void setUseBlur(boolean useBlur) {
        mUseBlur = useBlur;
    }

    /**
     * Method to set the use of renderScript algorithm
     *
     * @param useRenderScript if want to use renderScript algorithm
     */
    public void setUseRenderScript(boolean useRenderScript) {
        mUseRenderScript = useRenderScript;
    }

    /**
     * Select your preferred blur radius to apply
     * <p>
     * By default blur radius is set to {@link PickerUIBlur#MIN_BLUR_RADIUS}
     *
     * @param blurRadius The radius to blur the image, radius must be at least 1
     */
    public void setBlurRadius(int blurRadius) {
        if (!PickerUIBlur.isValidBlurRadius(blurRadius)) {
            throw new IllegalArgumentException("Invalid blur radius");
        }
        mBlurRadius = Math.max(blurRadius, PickerUIBlur.MIN_BLUR_RADIUS);
    }

    private void setAlpha(View view, float alpha, long durationMillis) {
        view.setAlpha(alpha);
    }

    /**
     * Select the color filter to the blur effect
     *
     * @param filterColor The color to overlay
     */
    public void setFilterColor(int filterColor) {
        this.mFilterColor = filterColor;
    }

    /**
     * This method puts a layer over the image if you choose to put a color filter.
     *
     * @param sourceBitmap the bitmap downscaled and blurred.
     * @param image        the ImageView at which to place the downscaled and blurred image
     * @param color        the filter color to apply over the image
     */
    private void changeBitmapColor(Bitmap sourceBitmap, ImageView image, int color) {

        Bitmap resultBitmap = Bitmap
                .createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1,
                        sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.OVERLAY);
        p.setColorFilter(filter);
        image.setImageBitmap(resultBitmap);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
    }

    /**
     * This method set the downscaled and blurred image in the fake ImageView, and with a filter
     * over the image if it's
     * necessary.
     *
     * @param blurBitmap the bitmap downscaled and blurred.
     */
    void setBackground(Bitmap blurBitmap) {
        BitmapDrawable bd = new BitmapDrawable(mContext.getResources(), blurBitmap);
        bd.setAlpha(mAlpha);
        if (mFilterColor != -1) {
            changeBitmapColor(bd.getBitmap(), mBlurredImageView, mFilterColor);
        } else {
            mBlurredImageView.setImageBitmap(blurBitmap);
        }
    }

    /**
     * This method shows the fake ImageView and set the new blurred image.
     *
     * @param bitmapWithBlur the bitmap downscaled and blurred.
     */
    public void showBlurImage(Bitmap bitmapWithBlur) {
        if (mUseBlur) {
            mBlurredImageView.setImageBitmap(null);
            mBlurredImageView.setVisibility(View.VISIBLE);

            // Set the blurred background
            setBackground(bitmapWithBlur);
        }
    }

    /**
     * This method hides the fake ImageView, the blurred image is recycled and the background of the fake ImageView is
     * cleared.
     */
    public void handleRecycle() {
        if (mUseBlur) {
            Drawable drawable = mBlurredImageView.getDrawable();

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
                Bitmap bitmap = bitmapDrawable.getBitmap();

                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            mBlurredImageView.setVisibility(View.GONE);
            mBlurredImageView.setImageBitmap(null);
        }
    }

    /**
     * Create a ImageView with width and height MATCH_PARENT, which will host the blurred snapshot.
     */
    private void createImageViewBlur() {

        if (mUseBlur) {
            mRootView = (ViewGroup) ((Activity) mContext).getWindow().getDecorView()
                    .findViewById(android.R.id.content);

            mBlurredImageView = new ImageView(mContext);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            Log.d("YYYYY width", "" + mRootView.getWidth());
            Log.d("YYYYY height", "" + mRootView.getHeight());
            mBlurredImageView.setLayoutParams(params);
            mBlurredImageView.setClickable(false);
            mBlurredImageView.setVisibility(View.GONE);
            mBlurredImageView.setScaleType(ImageView.ScaleType.FIT_XY);

            mRootView.post(() -> {
                // Add the ImageView with blurred view
                ((ViewGroup) mRootView.getChildAt(0))
                        .addView(mBlurredImageView, mRootView.getChildCount());
            });
        }
    }

    /**
     * This method first checks if it is necessary to perform the Blur task.
     * If it's necesarry, launch the BlurTask. If it's necessary, it notifies to show the panel.
     */
    public void render() {
        if (mUseBlur) {
            PickerUIBlurTask pickerUIBlurTask = new PickerUIBlurTask((Activity) mContext,
                    mBlurRadius,
                    mBlurFinishedListener, mUseRenderScript);
            pickerUIBlurTask.execute();
        } else {
            if (mBlurFinishedListener == null) {
                throw new IllegalStateException(
                        "You must assign a valid BlurFinishedListener first!");
            }
            mBlurFinishedListener.onBlurFinished(null);
        }
    }

    /**
     * Set a callback listener when blur has finished
     *
     * @param listener Callback instance.
     */
    public void setBlurFinishedListener(BlurFinishedListener listener) {
        this.mBlurFinishedListener = listener;
    }

    /**
     * Interface for a callback when blur has finished
     */
    public interface BlurFinishedListener {

        /**
         * Callback when the blur has finished
         *
         * @param bitmapWithBlur Return bitmap with blurr applied
         */
        void onBlurFinished(Bitmap bitmapWithBlur);
    }
}