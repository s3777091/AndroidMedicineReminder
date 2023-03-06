package com.dab.medireminder.dialog.picker.blur;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;

public class PickerUIBlurTask extends AsyncTask<Void, Void, Bitmap> {


    private final PickerUIBlurHelper.BlurFinishedListener mBlurFinishedListener;
    /**
     * Blur state
     *
     * If 'READY' we can render
     * if 'EXECUTING', we are rendering the background
     */
    private State mState = State.READY;
    /**
     * Bitmap to save the downscaled image
     */
    private Bitmap mBitmapDownscaled;
    private int mBlurRadius;
    private Activity activity;
    private boolean useRenderScript;


    /**
     * Constructor
     *
     * @param a                    Activity is necessary to snapshot the view
     * @param radius               the radius to apply in Blur task.
     * @param blurFinishedListener listener to notify when blur finished.
     */
    public PickerUIBlurTask(Activity a, int radius,
                            PickerUIBlurHelper.BlurFinishedListener blurFinishedListener, boolean useRenderScript) {
        activity = a;
        mBlurRadius = radius < 1 ? 1 : radius;
        mBlurFinishedListener = blurFinishedListener;
        this.useRenderScript = useRenderScript;
    }

    /**
     * Snapshots the specified layout with {@link PickerUIBlurHelper#loadBitmapFromView(View)} and
     * scale it using
     * {@link
     * PickerUIBlurHelper#downscaleBitmap(Bitmap)}()}
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mState.equals(State.READY)) {
            mState = State.EXECUTING;
            //The view that we take snapshot
            View snapshotView = activity.getWindow().getDecorView()
                    .findViewById(android.R.id.content);
            PickerUIBlurHelper pickerUIBlurHelper =  new PickerUIBlurHelper();
            Bitmap bitmapDecorView = pickerUIBlurHelper.loadBitmapFromView(snapshotView);
            if(bitmapDecorView!=null)
                mBitmapDownscaled = pickerUIBlurHelper.downscaleBitmap(bitmapDecorView);
        } else {
            cancel(true);
        }
    }

    /**
     * We blur the scaled bitmap with the preferred blur radius.
     * Process the image using renderscript if possible with
     * boolean)}
     *
     * @return blurred image
     */
    @Override
    protected Bitmap doInBackground(Void... params) {
        if (mState.equals(State.EXECUTING) && mBitmapDownscaled != null) {
            return Blur.apply(activity, mBitmapDownscaled, mBlurRadius, useRenderScript);
        } else {
            return null;
        }
    }

    /**
     * Finally, we post the blurred image in our listener
     *
     * @param blurredBitmap Blurred image in {@link PickerUIBlurTask#doInBackground(Void...)}
     */
    @Override
    protected void onPostExecute(Bitmap blurredBitmap) {

        super.onPostExecute(blurredBitmap);
        activity = null;
        if (mBlurFinishedListener == null) {
            throw new IllegalStateException("You must assign a valid BlurFinishedListener first!");
        }
        mBlurFinishedListener.onBlurFinished(blurredBitmap);

        mState = State.READY;
    }

    private enum State {
        READY,
        EXECUTING
    }
}
