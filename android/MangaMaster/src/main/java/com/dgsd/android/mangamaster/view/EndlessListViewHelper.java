package com.dgsd.android.mangamaster.view;

import android.database.DataSetObserver;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class EndlessListViewHelper implements AbsListView.OnScrollListener {
 
    /**
     * When the list is scrolled this percentage down, execute the callback;
     */
    private static final float ITEM_LOAD_CUTOFF = 0.8f;
 
    /**
     * We only want to callback <i>once</i> until the user scrolls up or the data is reloaded
     */
    private boolean mAcceptNextEndEvent;
 
    private int mLastCount = -1;
 
    private final ListView mList;
 
    private OnEndReachedListener mEndReachedListener;
 
    public EndlessListViewHelper(ListView list) {
        this(list, true);
    }
 
    public EndlessListViewHelper(ListView list, boolean useScrollPosition) {
        mList = list;
        if (list == null) {
            throw new IllegalArgumentException("List must not be null!");
        }
 
        if (useScrollPosition) {
            mList.setOnScrollListener(this);
        }
    }
 
    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (mLastCount != mList.getAdapter().getCount()) {
                mLastCount = mList.getAdapter().getCount();
                mAcceptNextEndEvent = false;
            }
        }
 
        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };
 
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //No-op
    }
 
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (mEndReachedListener == null || mAcceptNextEndEvent) {
            return;
        }
 
        if (hasReachedEnd(firstVisibleItem, visibleItemCount, totalItemCount)) {
            mAcceptNextEndEvent = true;
            mEndReachedListener.onEndReached(mList);
        }
    }
 
    protected boolean hasReachedEnd(int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        final int lastItemPos = firstVisibleItem + visibleItemCount;
        if (totalItemCount <= lastItemPos) {
            return false; //Dont callback if we can already fit everything on screen..
        }
 
        if (mList.isStackFromBottom()) {
            final int cutOff = (int) Math.ceil(totalItemCount * (1f - ITEM_LOAD_CUTOFF));
            return lastItemPos < cutOff || (firstVisibleItem == 0 && cutOff <= lastItemPos);
        } else {
            return lastItemPos > (int) (totalItemCount * ITEM_LOAD_CUTOFF);
        }
    }
 
    public boolean acceptNextEndEvent() {
        return mAcceptNextEndEvent;
    }
 
    public void setAcceptNextEndEvent(boolean accept) {
        mAcceptNextEndEvent = accept;
    }
 
    public void setOnEndReachedListener(OnEndReachedListener listener) {
        mEndReachedListener = listener;
    }
 
    public void onDetachedFromWindow() {
        try {
            if (mList.getAdapter() != null) {
                mList.getAdapter().unregisterDataSetObserver(mDataSetObserver);
            }
        } catch (Exception e) {
            //Unfortunately, Android doesnt provide a way to query where a DataSetObserver
            //is registered, so we must handle the exception..
        }
    }
 
    public void onPreSetAdapter(ListAdapter adapter) {
        if (mList.getAdapter() != null) {
            mList.getAdapter().unregisterDataSetObserver(mDataSetObserver);
        }
    }
 
    public void onPostSetAdapter(ListAdapter adapter) {
        if (adapter != null) {
            mLastCount = adapter.getCount();
            adapter.registerDataSetObserver(mDataSetObserver);
        }
    }
 
    /**
     * Listener for when the end of a list is reached
     */
    public interface OnEndReachedListener {
 
        public void onEndReached(ListView lv);
    }
}