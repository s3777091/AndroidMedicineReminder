package com.dab.medireminder.dialog.picker.blur;

public class PickerUIBlur {

    /**
     * Minimum valid value of Blur radius.
     */
    public static final int MIN_BLUR_RADIUS = 1;
    /**
     * Minimum valid value of down scale factor.
     */
    public static final float MIN_DOWNSCALE = 1.0f;
    /**
     * Maximum valid value of Blur radius.
     */
    private static final int MAX_BLUR_RADIUS = 25;
    /**
     * Maximum valid value of down scale factor.
     */
    private static final float MAX_DOWNSCALE = 6.0f;
    /**
     * Default behaviour of blur
     */
    public static final boolean DEFAULT_USE_BLUR = true;
    /**
     * Default use of renderscript algorithm
     */
    public static final boolean DEFAULT_USE_BLUR_RENDERSCRIPT = false;
    /**
     * Default Blur radius used for the background
     */
    public static final int DEFAULT_BLUR_RADIUS = 15;
    /**
     * Default Down scale factor to reduce blurring time and memory allocation.
     */
    public static final float DEFAULT_DOWNSCALE_FACTOR = 5.0f;
    /**
     * Default alpha to apply in blurred image
     */
    public static final int CONSTANT_DEFAULT_ALPHA = 100;

    /**
     * Validates if the radius value chosen is valid.
     *
     * @param value Radius value selected
     * @return Returns 'true' if the value is between {@link PickerUIBlur#MAX_BLUR_RADIUS} and
     * {@link
     * PickerUIBlur#MIN_BLUR_RADIUS}
     */
    public static boolean isValidBlurRadius(int value) {
        return value >= MIN_BLUR_RADIUS && value <= MAX_BLUR_RADIUS;
    }

    /**
     * Validates if the scale chosen is valid.
     *
     * @param value Scale value selected
     * @return Returns 'true' if the value is between {@link PickerUIBlur#MAX_DOWNSCALE} and {@link
     * PickerUIBlur#MIN_DOWNSCALE}
     */
    public static boolean isValidDownscale(float value) {
        return value >= MIN_DOWNSCALE && value <= MAX_DOWNSCALE;
    }
}