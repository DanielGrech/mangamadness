package com.dgsd.android.mangamaster.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.fragment.ChapterListFragment;
import com.dgsd.android.mangamaster.fragment.SeriesInfoFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 *
 */
public class SeriesActivity extends BaseActivity {

    public static final String EXTRA_SERIES_ID = "_series_id";

    @InjectView(R.id.sliding_panel)
    SlidingUpPanelLayout mSlidingPanel;

    private SeriesInfoFragment mSeriesInfoFragment;

    private ChapterListFragment mChapterListFragment;

    private String mSeriesId;

    private String mSeriesName;

    private SlidingUpPanelLayout.SimplePanelSlideListener mSlidingPanelListener
            = new SlidingUpPanelLayout.SimplePanelSlideListener() {
        @Override
        public void onPanelSlide(final View panel, final float slideOffset) {
            mSeriesInfoFragment.onPanelSlide(slideOffset);
            mChapterListFragment.onPanelSlide(slideOffset);
        }
    };

    @Override
    protected void onJobRequestStart(final String action) {
        if (!(mSeriesInfoFragment.onJobRequestStart(action)
                || mChapterListFragment.onJobRequestStart(action))) {
            // It doesn't belong to any of our fragments .. let's handle it ourselves

        }
    }

    @Override
    protected void onJobRequestFinish(final String action) {
        if (!(mSeriesInfoFragment.onJobRequestFinish(action)
                || mChapterListFragment.onJobRequestFinish(action))) {
            // It doesn't belong to any of our fragments .. let's handle it ourselves

        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_series);
        ButterKnife.inject(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mSeriesId = getIntent().getStringExtra(EXTRA_SERIES_ID);
        if (TextUtils.isEmpty(mSeriesId)) {
            throw new IllegalStateException("Need to pass a series id!");
        }

        setupTintManagerForViews(true, true, mSlidingPanel);
        setupFragments();
        setupViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    public void onBackPressed() {
        if (mSlidingPanel.isExpanded()) {
            mSlidingPanel.collapsePane();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        mSlidingPanel.setShadowDrawable(
                getResources().getDrawable(R.drawable.sliding_drawer_shadow));
        mSlidingPanel.setDragView(mSeriesInfoFragment.getDragView());
        mSlidingPanel.setPanelSlideListener(mSlidingPanelListener);

    }

    private void setupFragments() {
        mSeriesInfoFragment = findFragment(R.id.series_info_fragment);
        mSeriesInfoFragment.setSeries(mSeriesId);

        mChapterListFragment = findFragment(R.id.chapter_list_fragment);
        mChapterListFragment.setSeries(mSeriesId);
    }
}
