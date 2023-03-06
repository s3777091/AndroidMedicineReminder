package com.dab.medireminder.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;

import com.dab.medireminder.R;
import com.dab.medireminder.dialog.picker.PickerUI;
import com.dab.medireminder.dialog.picker.PickerUISettings;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class PickerTimerFragment extends BottomSheetDialogFragment {
    OnDismissListener onDismissListener;
    private int mPositionHour;
    private int mPositionMinute;

    @BindView(R.id.picker_hour)
    PickerUI pickerHour;

    @BindView(R.id.process_bar)
    ProgressBar process_bar;

    private final PickerUiListener pickerUiListener;
    public boolean isShow;
    private final ArrayList<String> optionsHour = new ArrayList<>();
    private final ArrayList<String> optionsMinute = new ArrayList<>();

    CoordinatorLayout.Behavior behavior;

    @SuppressLint("ValidFragment")
    public PickerTimerFragment(PickerUiListener pickerUiListener, int mPositionHour, int mPositionMinute) {
        this.pickerUiListener = pickerUiListener;
        this.mPositionHour = mPositionHour;
        this.mPositionMinute = mPositionMinute;
    }

    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_EXPANDED);
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_piker_timer, null);
        dialog.setContentView(contentView);
        ButterKnife.bind(this, contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        setPickerUi();
    }

    @SuppressLint("DefaultLocale")
    private void setUpData() {
        for (int i = 0; i < 60; i++) {
            optionsMinute.add(String.format("%02d", i));
            if (i < 24) {
                optionsHour.add(String.format("%02d", i));
            }
        }

    }

    private void setPickerUi() {
        if (getActivity() == null || pickerHour == null) {
            return;
        }

        setUpData();

        if (!optionsHour.isEmpty() && !optionsMinute.isEmpty()) {
            process_bar.setVisibility(View.GONE);

            pickerHour.mHiddenPanelPickerUI.setVisibility(View.VISIBLE);
            pickerHour.mChosseTv.setEnabled(true);
            PickerUISettings pickerHourSettings = new PickerUISettings.Builder()
                    .withItems(optionsHour)
                    .withItemsMinute(optionsMinute)
                    .withBackgroundColor(android.R.color.white)
                    .withAutoDismiss(true)
                    .withItemsClickables(false)
                    .withUseBlur(false)
                    .build();
            pickerHour.setSettings(pickerHourSettings);
            if (mPositionHour > -1) {
                pickerHour.slideUp(mPositionHour);
            } else {
                pickerHour.slideUp(0);
            }
            if (mPositionMinute > -1) {
                pickerHour.slideUpMinute(mPositionMinute);
            } else {
                pickerHour.slideUpMinute(0);
            }

        } else {
            process_bar.setVisibility(View.VISIBLE);
            pickerHour.mHiddenPanelPickerUI.setVisibility(View.GONE);
            pickerHour.mChosseTv.setEnabled(false);
        }

        pickerHour.setOnClickItemPickerUIListener(new PickerUI.PickerUIItemClickListener() {
            @Override
            public void onItemClickPickerUI(int which, int position, String valueResult) {
            }

            @Override
            public void onCloseClick(View view) {
                dismiss();
            }

            @Override
            public void onChooseClick(int which, int positionHour, int positionMinute) {
                dismiss();
                pickerUiListener.onChooseClick(Integer.parseInt(optionsHour.get(positionHour)),
                        Integer.parseInt(optionsMinute.get(positionMinute)),
                        positionHour, positionMinute);
            }
        });
    }

    public void setData(int positionHour, int positionMinute) {
        this.mPositionHour = positionHour;
        this.mPositionMinute = positionMinute;
        setPickerUi();
    }


    public interface PickerUiListener {
        void onChooseClick(int hour, int minute, int positionHour, int positionMinute);
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        if (isShow) return;

        super.show(manager, tag);
        isShow = true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        isShow = false;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
        super.onDismiss(dialog);
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
