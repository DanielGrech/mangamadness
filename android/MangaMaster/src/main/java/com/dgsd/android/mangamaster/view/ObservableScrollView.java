package com.dgsd.android.mangamaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 *
 */
public class ObservableScrollView extends ScrollView {

    private OnScrollChangeListener mOnScrollChangeListener;

    public ObservableScrollView(final Context context) {
        super(context);
    }

    public ObservableScrollView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableScrollView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mOnScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnScrollChangeListener(final OnScrollChangeListener listener) {
        mOnScrollChangeListener = listener;
    }

    public static interface OnScrollChangeListener {
        public void onScrollChanged(int left, int top, int oldLeft, int oldTop);
    }

}
