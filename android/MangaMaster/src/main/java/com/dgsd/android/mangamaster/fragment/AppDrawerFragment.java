package com.dgsd.android.mangamaster.fragment;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.dgsd.android.mangamaster.R;

/**
 *
 */
public class AppDrawerFragment extends BaseFragment {

    public static enum DrawerItem {
        LATEST,
        ALL_SERIES,
        FAVOURITES,
        DOWNLOADS
    }

    private static final SparseArray<DrawerItem> sViewIdToItemMap;

    static {
        sViewIdToItemMap = new SparseArray<>();
        sViewIdToItemMap.put(R.id.latest, DrawerItem.LATEST);
        sViewIdToItemMap.put(R.id.all_series, DrawerItem.ALL_SERIES);
        sViewIdToItemMap.put(R.id.favourites, DrawerItem.FAVOURITES);
        sViewIdToItemMap.put(R.id.downloads, DrawerItem.DOWNLOADS);
    }

    @InjectView(R.id.latest)
    TextView mLatest;

    @InjectView(R.id.all_series)
    TextView mAllSeries;

    @InjectView(R.id.favourites)
    TextView mFavourites;

    @InjectView(R.id.downloads)
    TextView mDownloads;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.frag_app_drawer, container, false);
        ButterKnife.inject(this, v);

        return v;
    }

    @SuppressWarnings("unused")
    @OnClick({
            R.id.latest,
            R.id.all_series,
            R.id.favourites,
            R.id.downloads
    })
    public void onItemClick(View view) {
        final DrawerItem item = sViewIdToItemMap.get(view.getId());
        mEventBus.post(new OnAppDrawerItemClickEvent(item));
    }

    public static class OnAppDrawerItemClickEvent {
        public final DrawerItem item;

        public OnAppDrawerItemClickEvent(DrawerItem item) {
            this.item = item;
        }
    }
}
