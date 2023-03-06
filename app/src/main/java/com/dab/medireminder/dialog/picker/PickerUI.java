package com.dab.medireminder.dialog.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dab.medireminder.R;
import com.dab.medireminder.dialog.picker.blur.PickerUIBlurHelper;

import java.util.Arrays;
import java.util.List;

import static com.dab.medireminder.dialog.picker.PickerUI.SLIDE.*;

/**
 *
 */

public class PickerUI extends RelativeLayout implements PickerUIBlurHelper.BlurFinishedListener {

    private static final String LOG_TAG = PickerUI.class.getSimpleName();

    private boolean autoDismiss = PickerUISettings.DEFAULT_AUTO_DISMISS;
    private boolean itemsClickables = PickerUISettings.DEFAULT_ITEMS_CLICKABLES;
    private boolean itemsClickablesMinute = PickerUISettings.DEFAULT_ITEMS_CLICKABLES;

    private PickerUIItemClickListener mPickerUIListener;
    public PickerUIListView mPickerUIListView;
    public PickerUIListView mPickerUIListViewMinute;
    public View mHiddenPanelPickerUI;
    public View mHiddenPanelPickerUIHour;
    public View mHiddenPanelPickerUIMinute;
    private final Context mContext;
    private List<String> items;
    private List<String> itemsMinute;
    private int position;
    private int positionMinute;
    private PickerUIBlurHelper mPickerUIBlurHelper;
    private int backgroundColorPanel;
    private int colorLines;
    private int mColorTextCenterListView;
    private int mColorTextNoCenterListView;
    private PickerUISettings mPickerUISettings;
    private TextView mCloseTv;
    public TextView mChosseTv;
    private TextView mTitleTv;
    int mWhich = 0;

    /**
     * Default constructor
     */
    public PickerUI(Context context) {
        super(context);
        mContext = context;
        if (isInEditMode()) {
            createEditModeView();
        } else {
            createView(null);
        }
    }

    /**
     * Default constructor
     */
    public PickerUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        if (isInEditMode()) {
            createEditModeView();
        } else {
            createView(attrs);
            getAttributes(attrs);
        }
    }

    /**
     * Default constructor
     */
    public PickerUI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        if (isInEditMode()) {
            createEditModeView();
        } else {
            createView(attrs);
            getAttributes(attrs);
        }
    }

    /**
     * This method inflates the panel to be visible from Preview Layout
     */
    private void createEditModeView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pickerui, this, true);
    }


    private void createView(AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pickerui, this, true);
        mHiddenPanelPickerUI = view.findViewById(R.id.hidden_panel);
        mHiddenPanelPickerUIHour = view.findViewById(R.id.hidden_panel_hour);
        mHiddenPanelPickerUIMinute = view.findViewById(R.id.hidden_panel_minute);
        mPickerUIListView =  view.findViewById(R.id.picker_ui_listview);
        mPickerUIListViewMinute=  view.findViewById(R.id.picker_ui_listview_minute);
        mChosseTv = view.findViewById(R.id.choose_tv);
        mCloseTv = view.findViewById(R.id.close_tv);
        mTitleTv = view.findViewById(R.id.title_tv);

        setItemsClickables(itemsClickables);
        setItemsClickablesMinute(itemsClickablesMinute);
        mPickerUIBlurHelper = new PickerUIBlurHelper(mContext, attrs);
        mPickerUIBlurHelper.setBlurFinishedListener(this);
    }

    /**
     * Retrieve styles attributes
     */
    private void getAttributes(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.PickerUI, 0, 0);

        try {
            autoDismiss = typedArray
                    .getBoolean(R.styleable.PickerUI_autoDismiss,
                            PickerUISettings.DEFAULT_AUTO_DISMISS);
            itemsClickables = typedArray
                    .getBoolean(R.styleable.PickerUI_itemsClickables,
                            PickerUISettings.DEFAULT_ITEMS_CLICKABLES);
            itemsClickablesMinute = typedArray
                    .getBoolean(R.styleable.PickerUI_itemsClickables,
                            PickerUISettings.DEFAULT_ITEMS_CLICKABLES);
            backgroundColorPanel = typedArray.getColor(R.styleable.PickerUI_backgroundColor,
                    getResources().getColor(R.color.background_panel_pickerui));
            colorLines = typedArray.getColor(R.styleable.PickerUI_linesCenterColor,
                    getResources().getColor(R.color.lines_panel_pickerui));
            mColorTextCenterListView = typedArray
                    .getColor(R.styleable.PickerUI_textCenterColor,
                            getResources().getColor(R.color.text_center_pickerui));
            mColorTextNoCenterListView = typedArray
                    .getColor(R.styleable.PickerUI_textNoCenterColor,
                            getResources().getColor(R.color.text_no_center_pickerui));

            int idItems;
            idItems = typedArray.getResourceId(R.styleable.PickerUI_entries, -1);
            if (idItems != -1) {
                setItems(mContext, Arrays.asList(getResources().getStringArray(idItems)));
                setItemsMinute(mContext, Arrays.asList(getResources().getStringArray(idItems)));
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error while creating the view PickerUI: ", e);
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Slide the panel depending on the current state.
     * If slide up, the position is the half of the elements.
     */
    public void slide() {
        int position = 0;
        if (items != null) {
            position = items.size() / 2;
        }
        slide(position);
    }

    /**
     * Slide the panel depending depending on the current state.
     * If slide up, the position is the value selected.
     *
     * @param position the position to set in the center of the panel.
     */
    public void slide(final int position) {
//        if (!isPanelShown()) {
        slideUp(position);
//        } else {
        // Hide the Panel
//            hidePanelPickerUI();
//        }
    }

    /**
     * Slide the panel to the desired direction.
     *
     * @param slide the movement of the slide. See {@link SLIDE}
     */
    public void slide(SLIDE slide) {
        if (slide == UP) {
            if (!isPanelShown()) {
                int position = 0;
                if (items != null) {
                    position = items.size() / 2;
                }
                slideUp(position);
            }
        } else {
            // Hide the Panel
            hidePanelPickerUI();
        }
    }

    /**
     * Show the panel to the position selected.
     *
     * @param position the position to set in the center of the panel.
     */
    public void slideUp(int position) {
        //Render to do the blur effect
        this.position = position;
        mPickerUIBlurHelper.render();
    }

    public void slideUpMinute(int position) {
        //Render to do the blur effect
        this.positionMinute = position;
        mPickerUIBlurHelper.render();
    }

    /**
     * Hide the panel and clear blur image.
     */
    private void hidePanelPickerUI() {
        Animation bottomDown = AnimationUtils
                .loadAnimation(mContext, R.anim.picker_panel_bottom_down);
        mHiddenPanelPickerUI.startAnimation(bottomDown);

        bottomDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // Hide the panel
                mHiddenPanelPickerUI.setVisibility(View.GONE);

                // Clear blur image.
                mPickerUIBlurHelper.handleRecycle();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    /**
     * Method to set if hide the panel when you click an item
     *
     * @param autoDismiss the behaviour selected to hide the panel
     */
    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }

    /**
     * Sets the background color for the panel.
     *
     * @param color the color of the background
     */
    public void setBackgroundColorPanel(int color) {
        backgroundColorPanel = color;
    }

    /**
     * Sets the color of the lines of the center
     *
     * @param color the color of the lines
     */
    public void setLinesColor(int color) {
        colorLines = color;
    }

    /**
     * Method to enable the click of items
     *
     * @param itemsClickables the behaviour selected for items
     */
    public void setItemsClickables(boolean itemsClickables) {
        this.itemsClickables = itemsClickables;
        if (mPickerUIListView != null && mPickerUIListView.getPickerUIAdapter() != null) {
            mPickerUIListView.getPickerUIAdapter().setItemsClickables(itemsClickables);
        }
    }

    public void setItemsClickablesMinute(boolean itemsClickables) {
        this.itemsClickablesMinute = itemsClickables;
        if (mPickerUIListViewMinute != null && mPickerUIListViewMinute.getPickerUIAdapter() != null) {
            mPickerUIListViewMinute.getPickerUIAdapter().setItemsClickables(itemsClickables);
        }
    }

    private void setTextColorsListView() {
        setColorTextCenter(mColorTextCenterListView);
        setColorTextNoCenter(mColorTextNoCenterListView);
    }

    public boolean isPanelShown() {
        return mHiddenPanelPickerUI.getVisibility() == View.VISIBLE;
    }

    /**
     * Method to set items to show in panel.
     * In this method, by default, the 'which' is 0 and the position is the half of the elements.
     *
     * @param items elements to show in panel
     */
    public void setItems(Context context, List<String> items) {
        if (items != null) {
            setItems(context, items, 0, items.size() / 2);
        }
    }

    public void setItemsMinute(Context context, List<String> items) {
        if (items != null) {
            setItemsMinute(context, items, 0, items.size() / 2);
        }
    }

    /**
     * Method to set items to show in panel.
     *
     * @param context  {@link PickerUIListView} needs a context
     * @param items    elements to show in panel
     * @param which    id of the element has been clicked
     * @param position the position to set in the center of the panel.
     */
    public void setItems(Context context, List<String> items, int which, int position) {
        if (items != null) {
            this.items = items;
            mPickerUIListView.setItems(context, items, which, position, itemsClickables);
            setTextColorsListView();
        }
    }

    public void setItemsMinute(Context context, List<String> items, int which, int position) {
        if (items != null) {
            this.itemsMinute = items;
            mPickerUIListViewMinute.setItems(context, items, which, position, itemsClickablesMinute);
            setTextColorsListView();
        }
    }

    /**
     * This method will be run after you have the bitmap with the blur effect done (or not).
     */
    @Override
    public void onBlurFinished(Bitmap bitmapWithBlur) {

        if (bitmapWithBlur != null) {
            mPickerUIBlurHelper.showBlurImage(bitmapWithBlur);
        }

        // Show the panel
        showPanelPickerUI();
    }

    /**
     * Method to set the use of blur effect
     *
     * @param useBlur if want to use blur
     */
    public void setUseBlur(boolean useBlur) {
        if (mPickerUIBlurHelper != null) {
            mPickerUIBlurHelper.setUseBlur(useBlur);
        }
    }

    /**
     * Method to set the use of renderScript algorithm
     *
     * @param useRenderScript if want to use renderScript algorithm
     */
    public void setUseRenderScript(boolean useRenderScript) {
        if (mPickerUIBlurHelper != null) {
            mPickerUIBlurHelper.setUseRenderScript(useRenderScript);
        }
    }

    /**
     * Apply custom down scale factor
     *
     * @param downScaleFactor Factor customized down scale factor, must be at least 1.0
     */
    public void setDownScaleFactor(float downScaleFactor) {
        if (mPickerUIBlurHelper != null) {
            mPickerUIBlurHelper.setDownScaleFactor(downScaleFactor);
        }
    }

    /**
     * Select your preferred blur radius to apply
     *
     * @param radius The radius to blur the image, radius must be at least 1
     */
    public void setBlurRadius(int radius) {
        if (mPickerUIBlurHelper != null) {
            mPickerUIBlurHelper.setBlurRadius(radius);
        }
    }

    /**
     * Select the color filter to the blur effect
     *
     * @param filterColor The color to overlay
     */
    public void setFilterColor(int filterColor) {
        if (mPickerUIBlurHelper != null) {
            mPickerUIBlurHelper.setFilterColor(filterColor);
        }
    }

    /**
     * Sets the text color for the item of the center.
     *
     * @param color the color of the text
     */
    public void setColorTextCenter(int color) {
        if (mPickerUIListView != null && mPickerUIListView.getPickerUIAdapter() != null) {

            int newColor;
            try {
                newColor = getResources().getColor(color);
            } catch (Resources.NotFoundException e) {
                newColor = color;
            }
            mColorTextCenterListView = newColor;
            mPickerUIListView.getPickerUIAdapter().setColorTextCenter(newColor);
        }

        if (mPickerUIListViewMinute != null && mPickerUIListViewMinute.getPickerUIAdapter() != null) {

            int newColor;
            try {
                newColor = getResources().getColor(color);
            } catch (Resources.NotFoundException e) {
                newColor = color;
            }
            mColorTextCenterListView = newColor;
            mPickerUIListViewMinute.getPickerUIAdapter().setColorTextCenter(newColor);
        }
    }

    /**
     * Sets the text color for the items which aren't in the center.
     *
     * @param color the color of the text
     */
    public void setColorTextNoCenter(int color) {
        if (mPickerUIListView != null && mPickerUIListView.getPickerUIAdapter() != null) {
            int newColor;
            try {
                newColor = getResources().getColor(color);
            } catch (Resources.NotFoundException e) {
                newColor = color;
            }
            mColorTextNoCenterListView = newColor;
            mPickerUIListView.getPickerUIAdapter().setColorTextNoCenter(newColor);
        }

        if (mPickerUIListViewMinute != null && mPickerUIListViewMinute.getPickerUIAdapter() != null) {
            int newColor;
            try {
                newColor = getResources().getColor(color);
            } catch (Resources.NotFoundException e) {
                newColor = color;
            }
            mColorTextNoCenterListView = newColor;
            mPickerUIListViewMinute.getPickerUIAdapter().setColorTextNoCenter(newColor);
        }
    }

    /**
     * Method to slide up the panel. Panel displays with an animation, and when it starts, the item
     * of the center is
     * selected.
     */
    private void showPanelPickerUI() {
        mHiddenPanelPickerUI.setVisibility(View.VISIBLE);
        setBackgroundPanel();
        setBackgroundLines();
        Animation bottomUp = AnimationUtils.loadAnimation(mContext, R.anim.picker_panel_bottom_up);
        mHiddenPanelPickerUIHour.startAnimation(bottomUp);
        bottomUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mPickerUIListView != null && mPickerUIListView.getPickerUIAdapter() != null) {
                    mPickerUIListView.getPickerUIAdapter().handleSelectEvent(position + 2);
                    mPickerUIListView.setSelection(position);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        Animation bottomUp2 = AnimationUtils.loadAnimation(mContext, R.anim.picker_panel_bottom_up);
        mHiddenPanelPickerUIMinute.startAnimation(bottomUp2);
        bottomUp2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mPickerUIListViewMinute != null && mPickerUIListViewMinute.getPickerUIAdapter() != null) {
                    mPickerUIListViewMinute.getPickerUIAdapter().handleSelectEvent(positionMinute + 2);
                    mPickerUIListViewMinute.setSelection(positionMinute);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }


    private void setBackgroundPanel() {
        int color;
        try {
            color = getResources().getColor(backgroundColorPanel);
        } catch (Resources.NotFoundException e) {
            color = backgroundColorPanel;
        }
        mHiddenPanelPickerUIHour.setBackgroundColor(color);
        mHiddenPanelPickerUIMinute.setBackgroundColor(color);
    }

    private void setBackgroundLines() {
        int color;
        try {
            color = getResources().getColor(colorLines);
        } catch (Resources.NotFoundException e) {
            color = colorLines;
        }

        //Top line
        mHiddenPanelPickerUIMinute.findViewById(R.id.picker_line_top_minute).setBackgroundColor(color);
        mHiddenPanelPickerUIHour.findViewById(R.id.picker_line_top).setBackgroundColor(color);

        //Bottom line
        mHiddenPanelPickerUIHour.findViewById(R.id.picker_line_bottom).setBackgroundColor(color);
        mHiddenPanelPickerUIMinute.findViewById(R.id.picker_line_bottom_minute).setBackgroundColor(color);
    }

    /**
     * Set a callback listener for the item click.
     *
     * @param listener Callback instance.
     */
    public void setOnClickItemPickerUIListener(final PickerUIItemClickListener listener) {
        this.mPickerUIListener = listener;


        mPickerUIListView.setOnClickItemPickerUIListener(
                (which, position, valueResult) -> {
                });

        mPickerUIListViewMinute.setOnClickItemPickerUIListener(
                (which, position, valueResult) -> {
                });



        mCloseTv.setOnClickListener(view -> mPickerUIListener.onCloseClick(view));

        mChosseTv.setOnClickListener(view -> mPickerUIListener.onChooseClick(mWhich, mPickerUIListView.getItemInListCenter(), mPickerUIListViewMinute.getItemInListCenter()));
    }

    /**
     * This method sets the desired functionalities of panel to make easy.
     *
     * @param pickerUISettings Object with all functionalities to make easy.
     */
    public void setSettings(PickerUISettings pickerUISettings) {
        mPickerUISettings = pickerUISettings;
        setColorTextCenter(pickerUISettings.getColorTextCenter());
        setColorTextNoCenter(pickerUISettings.getColorTextNoCenter());
        setItems(mContext, pickerUISettings.getItems());
        setItemsMinute(mContext, pickerUISettings.getItemsMinute());
        setBackgroundColorPanel(pickerUISettings.getBackgroundColor());
        setLinesColor(pickerUISettings.getLinesColor());
        setItemsClickables(pickerUISettings.areItemsClickables());
        setItemsClickablesMinute(pickerUISettings.areItemsClickablesMinute());
        setUseBlur(pickerUISettings.isUseBlur());
        setUseRenderScript(pickerUISettings.isUseBlurRenderscript());
        setAutoDismiss(pickerUISettings.isAutoDismiss());
        setBlurRadius(pickerUISettings.getBlurRadius());
        setDownScaleFactor(pickerUISettings.getBlurDownScaleFactor());
        setFilterColor(pickerUISettings.getBlurFilterColor());
    }

    /**
     * Save the state of the panel when orientation screen changed.
     */
    @Override
    public Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putParcelable("stateSettings", mPickerUISettings);
        //save everything
        bundle.putBoolean("stateIsPanelShown", isPanelShown());
        bundle.putInt("statePosition", mPickerUIListView.getItemInListCenter());
        bundle.putInt("statePositionMinute", mPickerUIListViewMinute.getItemInListCenter());
        return bundle;
    }

    /**
     * Retrieve the state of the panel when orientation screen changed.
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            //load everything
            PickerUISettings pickerUISettings = bundle.getParcelable("stateSettings");
            if (pickerUISettings != null) {
                setSettings(pickerUISettings);
            }

            boolean stateIsPanelShown = bundle.getBoolean("stateIsPanelShown");
            if (stateIsPanelShown) {

                final int statePosition = bundle.getInt("statePosition");
                final int statePositionMinute = bundle.getInt("statePositionMinute");

                ViewTreeObserver observer = getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressLint("ObsoleteSdkInt")
                    @Override
                    public void onGlobalLayout() {

                        slideUp(statePosition);

                        slideUp(statePositionMinute);

                        if (android.os.Build.VERSION.SDK_INT
                                >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            //noinspection deprecation
                            getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                    }
                });

            }
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    public enum SLIDE {
        UP,
        DOWN
    }

    /**
     * Interface for a callback when the item has been clicked.
     */
    public interface PickerUIItemClickListener {

        /**
         * Callback when the item has been clicked.
         *
         * @param which       id of the element has been clicked
         * @param position    Position of the current item.
         * @param valueResult Value of text of the current item.
         */
        void onItemClickPickerUI(int which, int position, String valueResult);

        void onCloseClick(View view);

        void onChooseClick(int which, int positionHour, int positionMinute);
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

}