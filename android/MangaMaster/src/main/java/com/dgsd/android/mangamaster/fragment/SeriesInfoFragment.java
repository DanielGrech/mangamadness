package com.dgsd.android.mangamaster.fragment;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.data.SeriesLoader;
import com.dgsd.android.mangamaster.model.MangaSeries;
import com.dgsd.android.mangamaster.view.SlidePanel;
import com.dgsd.android.mangamaster.view.UrlImageView;

import java.util.List;

public class SeriesInfoFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<List<MangaSeries>>, SlidePanel {

    private static final int LOADER_ID_SERIES = 0x2;

    protected static final AccelerateInterpolator sFadeInterpolator = new AccelerateInterpolator(3f);

    private String mSeriesId;

    private MangaSeries mSeries;

    @InjectView(R.id.cover_image_large)
    UrlImageView mCoverImageLarge;

    @InjectView(R.id.author_title)
    TextView mAuthorTitle;

    @InjectView(R.id.author)
    TextView mAuthor;

    @InjectView(R.id.artist_title)
    TextView mArtistTitle;

    @InjectView(R.id.artist)
    TextView mArtist;

    @InjectView(R.id.release_year_title)
    TextView mReleaseYearTitle;

    @InjectView(R.id.release_year)
    TextView mReleaseYear;

    @InjectView(R.id.genres_title)
    TextView mGenresTitle;

    @InjectView(R.id.genres)
    TextView mGenres;

    @InjectView(R.id.summary_title)
    TextView mSummaryTitle;

    @InjectView(R.id.summary)
    TextView mSummary;

    @InjectView(R.id.drag_view)
    View mDragView;

    @InjectView(R.id.cover_image_small)
    UrlImageView mCoverImageSmall;

    @InjectView(R.id.title_small)
    TextView mTitleSmall;

    @InjectView(R.id.latest_update)
    TextView mLatestUpdate;

    private boolean mHasReachedEndFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.frag_series_info, container, false);
        ButterKnife.inject(this, v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload(LOADER_ID_SERIES, this);
    }

    public void setSeries(String seriesId) {
        mSeriesId = seriesId;
        reload(LOADER_ID_SERIES, this);
    }

    @Override
    public Loader<List<MangaSeries>> onCreateLoader(final int id, final Bundle args) {
        switch (id) {
            case LOADER_ID_SERIES: {
                return new SeriesLoader(getActivity(), true, mSeriesId);
            }

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(final Loader<List<MangaSeries>> loader, final List<MangaSeries> series) {
        if (series != null && series.size() == 1) {
            populate(series.get(0));
        }
    }

    @Override
    public void onLoaderReset(final Loader loader) {

    }

    @Override
    public void onPanelSlide(final float offset) {
        mDragView.setAlpha(sFadeInterpolator.getInterpolation(offset));
    }

    public View getDragView() {
        return mDragView;
    }

    private void populate(MangaSeries series) {
        mSeries = series;
        if (series != null) {
            // Set our activity title
            final ActionBar ab = getBaseActivity().getActionBar();
            if (ab != null) {
                ab.setTitle(series.getName());
            }

            mCoverImageLarge.setImageUrl(series.getCoverImageUrl());
            mCoverImageSmall.setImageUrl(series.getCoverImageUrl());

            mTitleSmall.setText(series.getName());
//            mLatestUpdate.setText(); TODO: Add time updated to model

            setTextOrHide(series.getAuthor(), mAuthor, mAuthorTitle);
            setTextOrHide(series.getArtist(), mArtist, mArtistTitle);
            setTextOrHide(series.getYearOfRelease() > 0 ?
                    String.valueOf(series.getYearOfRelease()) : null, mReleaseYear, mReleaseYearTitle);
            setTextOrHide(series.getSummary(), mSummary, mSummaryTitle);

            String genresStr = null;
            if (series.getGenres() != null && !series.getGenres().isEmpty()) {
                genresStr = TextUtils.join(", ", series.getGenres());
            }
            setTextOrHide(genresStr, mGenres, mGenresTitle);
        }
    }

    private void setTextOrHide(String text, TextView valueView, TextView titleView) {
        if (TextUtils.isEmpty(text)) {
            titleView.setVisibility(View.GONE);
            valueView.setVisibility(View.GONE);
        } else {
            titleView.setVisibility(View.VISIBLE);
            valueView.setVisibility(View.VISIBLE);

            valueView.setText(text);
        }
    }
}
