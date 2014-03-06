package com.dgsd.android.mangamaster.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.data.SeriesLoader;
import com.dgsd.android.mangamaster.model.MangaSeries;
import com.dgsd.android.mangamaster.util.Api;
import com.dgsd.android.mangamaster.util.UiUtils;
import com.dgsd.android.mangamaster.view.SeriesListItemView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;

import static com.dgsd.android.mangamaster.data.SeriesLoader.Sort;

public class SeriesListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<MangaSeries>> {

    public static enum DisplayType {
        ALPHA, LATEST
    }

    private static final String KEY_DISPLAY_TYPE = "_display_type";

    private static final int LOADER_ID_SERIES = 0;

    private DisplayType mDisplayType;

    @InjectView(R.id.list)
    ListView mList;

    private SeriesListAdapter mAdapter;

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
    }

    @Override
    public Loader<List<MangaSeries>> onCreateLoader(final int id, final Bundle args) {
        return new SeriesLoader(getActivity(),
                mDisplayType == DisplayType.LATEST ? Sort.LATEST : Sort.ALPHA);
    }

    @Override
    public void onLoadFinished(final Loader<List<MangaSeries>> loader, final List<MangaSeries> series) {
        mAdapter.setSeries(series);
    }

    @Override
    public void onLoaderReset(final Loader<List<MangaSeries>> loader) {

    }

    private class SeriesListAdapter extends BaseAdapter {

        private List<MangaSeries> mSeries;

        @Override
        public int getCount() {
            return mSeries == null ? 0 : mSeries.size();
        }

        @Override
        public MangaSeries getItem(final int position) {
            return mSeries.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return mSeries.get(position).getId();
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

        public void setSeries(List<MangaSeries> series) {
            mSeries = series;
            notifyDataSetChanged();
        }
    }
}
