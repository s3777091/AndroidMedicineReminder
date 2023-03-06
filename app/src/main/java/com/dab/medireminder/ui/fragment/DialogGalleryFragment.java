package com.dab.medireminder.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;

import com.dab.medireminder.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;


@SuppressLint("NonConstantResourceId")
public class DialogGalleryFragment extends BottomSheetDialogFragment {
    OnDismissListener onDismissListener;
    private final GalleryListener galleryListener;
    public boolean isShow;

    public static final String GALLERY = "GALLERY";
    public static final String CAMERA = "CAMERA";

    CoordinatorLayout.Behavior behavior;

    @SuppressLint("ValidFragment")
    public DialogGalleryFragment(GalleryListener galleryListener) {
        this.galleryListener = galleryListener;
    }

    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                ((BottomSheetBehavior<?>) behavior).setState(BottomSheetBehavior.STATE_EXPANDED);
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
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_gallery, null);
        dialog.setContentView(contentView);
        ButterKnife.bind(this, contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    public interface GalleryListener {
        void onChooseAction(String action);
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

    @OnClick({R.id.ll_gallery, R.id.ll_camera, R.id.close_tv})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.ll_camera:
                dismiss();
                galleryListener.onChooseAction(CAMERA);
                break;
            case R.id.ll_gallery:
                dismiss();
                galleryListener.onChooseAction(GALLERY);
                break;
            case R.id.close_tv:
                dismiss();
                break;
        }
    }
}
