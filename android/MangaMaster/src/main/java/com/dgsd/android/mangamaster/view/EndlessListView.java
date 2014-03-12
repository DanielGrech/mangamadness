package com.dgsd.android.mangamaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;

/**
 *
 */
public class EndlessListView extends FadeInListView implements AbsListView.OnScrollListener {

    private boolean mIsPullToRefreshEnabled;

    public EndlessListView(final Context context) {
        super(context);
    }

    public EndlessListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public EndlessListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    {
        mIsPullToRefreshEnabled = true;
        setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(final AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }

    public boolean isPullToRefreshEnabled() {
        return mIsPullToRefreshEnabled;
    }

    public void setPullToRefreshEnabled(final boolean isPullToRefreshEnabled) {
        mIsPullToRefreshEnabled = isPullToRefreshEnabled;
    }

    public interface OnEndlessScrollListener {
        public void onScrolledToEnd(EndlessListView view);
    }
}
