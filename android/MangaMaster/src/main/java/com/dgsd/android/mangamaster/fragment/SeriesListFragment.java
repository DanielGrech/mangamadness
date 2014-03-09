package com.dgsd.android.mangamaster.fragment;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.activity.SeriesActivity;
import com.dgsd.android.mangamaster.data.SeriesLoader;
import com.dgsd.android.mangamaster.jobs.GetSeriesListJob;
import com.dgsd.android.mangamaster.model.MangaSeries;
import com.dgsd.android.mangamaster.util.Api;
import com.dgsd.android.mangamaster.util.CollectionBaseAdapter;
import com.dgsd.android.mangamaster.util.UiUtils;
import com.dgsd.android.mangamaster.view.SeriesListItemView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;

import static com.dgsd.android.mangamaster.data.SeriesLoader.Sort;

public class SeriesListFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<List<MangaSeries>> {

    private static final int SERIES_REQUEST_LIMIT = 100;

    public static enum DisplayType {
        ALPHA, LATEST
    }

    private static final String KEY_DISPLAY_TYPE = "_display_type";

    private static final int LOADER_ID_SERIES = 0;

    private DisplayType mDisplayType;

    @InjectView(R.id.list)
    ListView mList;

    private SeriesListAdapter mAdapter;

    private boolean mHasMadeInitialApiRequest;

    public static SeriesListFragment create(DisplayType type) {
        SeriesListFragment frag = new SeriesListFragment();

        Bundle args = new Bundle();
        args.putSerializable(KEY_DISPLAY_TYPE, type);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDisplayType = (DisplayType) getArguments().getSerializable(KEY_DISPLAY_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.frag_series_list, container, false);
        ButterKnife.inject(this, v);

        if (Api.isMin(Api.KITKAT) && UiUtils.isPortrait(getActivity())
                && !UiUtils.isTablet(getActivity())) {
            final SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
            final int navBarHeight = tintManager.getConfig().getNavigationBarHeight();

            final View footer = new View(getActivity());
            final ListView.LayoutParams lps
                    = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  navBarHeight);
            footer.setLayoutParams(lps);
            mList.addFooterView(footer);
        }

        mAdapter = new SeriesListAdapter();
        mList.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload(LOADER_ID_SERIES, this);

        if (!mHasMadeInitialApiRequest) {
            loadSeries(SERIES_REQUEST_LIMIT, 0, 0);
            mHasMadeInitialApiRequest = true;
        }
    }

    @Override
    public Loader<List<MangaSeries>> onCreateLoader(final int id, final Bundle args) {
        return new SeriesLoader(getActivity(), false,
                mDisplayType == DisplayType.LATEST ? Sort.LATEST : Sort.ALPHA);
    }

    @Override
    public void onLoadFinished(final Loader<List<MangaSeries>> loader, final List<MangaSeries> series) {
        mAdapter.setItems(series);
    }

    @Override
    public void onLoaderReset(final Loader<List<MangaSeries>> loader) {

    }

    @OnItemClick(R.id.list)
    public void onSeriesClicked(int position) {
        final MangaSeries series = mAdapter.getItem(position);

        final Intent intent = new Intent(getActivity(), SeriesActivity.class);
        intent.putExtra(SeriesActivity.EXTRA_SERIES_ID, series.getSeriesId());
        startActivity(intent);
    }

    private void loadSeries(int limit, int offset, long since) {
        mJobManager.addJobInBackground(new GetSeriesListJob(limit, offset, since));
    }

    private class SeriesListAdapter extends CollectionBaseAdapter<MangaSeries> {

        @Override
        public long getItemId(final int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final SeriesListItemView seriesView;
            if (convertView == null) {
                seriesView = (SeriesListItemView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.li_series, parent, false);
            } else {
                seriesView = (SeriesListItemView) convertView;
            }

            seriesView.populate(getItem(position));
            return seriesView;
        }
    }
}
