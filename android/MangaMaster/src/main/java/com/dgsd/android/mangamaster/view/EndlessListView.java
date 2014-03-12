package com.dgsd.android.mangamaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;

public class EndlessListView extends FadeInListView {

    private EndlessListViewHelper mEndlessHelper;

    public EndlessListView(Context context) {
        super(context);
    }

    public EndlessListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EndlessListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    {
        mEndlessHelper = new EndlessListViewHelper(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mEndlessHelper.onPreSetAdapter(adapter);
        super.setAdapter(adapter);
        mEndlessHelper.onPostSetAdapter(adapter);
    }

    @Override
    protected void onDetachedFromWindow() {
        mEndlessHelper.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    public void setOnEndReachedListener(EndlessListViewHelper.OnEndReachedListener listener) {
        mEndlessHelper.setOnEndReachedListener(listener);
    }
}