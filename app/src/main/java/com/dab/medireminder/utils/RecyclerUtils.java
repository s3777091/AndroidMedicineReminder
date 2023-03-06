package com.dab.medireminder.utils;


import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Collection Utils
 */
public class RecyclerUtils {

    private RecyclerUtils() {

    }

    //Has Fixed size because setting don't add wrap content
    public static void setupVerticalRecyclerView(Context context, RecyclerView mRecyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setHasFixedSize(true);
    }

    public static void setupHorizontalRecyclerView(Context context, RecyclerView mRecyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setHasFixedSize(true);
    }

    public static void setMaxHeight(final RecyclerView view, final int maxItem) {
        setMaxHeight(view, 1, maxItem);
    }

    public static void setMaxHeight(final RecyclerView view, final int minItem, final int maxItem) {
        view.post(() -> {
            RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
            assert layoutManager != null;
            if (layoutManager.getItemCount() <= minItem) {
                return;
            }
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = layoutManager.getHeight() * maxItem;
            view.setLayoutParams(layoutParams);
        });
    }

    /**
     * Common Setup for grid recycler view
     */
    public static void setupGridRecyclerView(Context context, RecyclerView mRecyclerView, int spanCount) {
        LinearLayoutManager layoutManager = new GridLayoutManager(context, spanCount);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
    }
}
