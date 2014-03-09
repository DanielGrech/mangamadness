package com.dgsd.android.mangamaster.util;

import android.widget.BaseAdapter;

import java.util.List;

/**
 *
 */
public abstract class CollectionBaseAdapter<T> extends BaseAdapter {

    protected List<T> mItems;

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public T getItem(final int position) {
        return mItems.get(position);
    }

    public void setItems(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }
}
