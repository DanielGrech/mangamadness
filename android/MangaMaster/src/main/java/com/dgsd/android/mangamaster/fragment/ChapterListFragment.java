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
import com.dgsd.android.mangamaster.activity.ChapterActivity;
import com.dgsd.android.mangamaster.data.ChapterLoader;
import com.dgsd.android.mangamaster.data.SeriesLoader;
import com.dgsd.android.mangamaster.jobs.GetChapterListJob;
import com.dgsd.android.mangamaster.model.MangaChapter;
import com.dgsd.android.mangamaster.model.MangaSeries;
import com.dgsd.android.mangamaster.util.CollectionBaseAdapter;
import com.dgsd.android.mangamaster.view.ChapterListItemView;
import com.dgsd.android.mangamaster.view.SlidePanel;
import com.path.android.jobqueue.JobManager;

import javax.inject.Inject;
import java.util.List;

/**
 *
 */
public class ChapterListFragment extends BaseFragment implements SlidePanel {

    private static final int LOADER_ID_CHAPTERS = 1;
    private static final int LOADER_ID_SERIES = 2;

    private String mSeriesId;

    private MangaSeries mSeries;

    @Inject
    JobManager mJobManager;

    @InjectView(R.id.list)
    ListView mList;

    private ChapterListAdapter mAdapter;

    private LoaderManager.LoaderCallbacks<List<MangaChapter>> mChapterLoader
            = new LoaderManager.LoaderCallbacks<List<MangaChapter>>() {
        @Override
        public Loader<List<MangaChapter>> onCreateLoader(final int id, final Bundle args) {
            return new ChapterLoader(getActivity(), mSeriesId);
        }

        @Override
        public void onLoadFinished(final Loader<List<MangaChapter>> loader, final List<MangaChapter> chapters) {
            mAdapter.setItems(chapters);
        }

        @Override
        public void onLoaderReset(final Loader<List<MangaChapter>> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<List<MangaSeries>> mSeriesLoader
            = new LoaderManager.LoaderCallbacks<List<MangaSeries>>() {
        @Override
        public Loader<List<MangaSeries>> onCreateLoader(final int id, final Bundle args) {
            return new SeriesLoader(getActivity(), mSeriesId);
        }

        @Override
        public void onLoadFinished(final Loader<List<MangaSeries>> loader, final List<MangaSeries> series) {
            mSeries = series == null || series.isEmpty() ? null : series.get(0);
            if (mSeries != null) {
                //TODO: Load from server!
            }
        }

        @Override
        public void onLoaderReset(final Loader<List<MangaSeries>> loader) {

        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.frag_chapter_list, container, false);
        ButterKnife.inject(this, v);

        mAdapter = new ChapterListAdapter();
        mList.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadChapters();
        reloadSeries();
    }

    @OnItemClick(R.id.list)
    public void onChapterClicked(int position) {
        final MangaChapter chapter = mAdapter.getItem(position);

        final Intent intent = new Intent(getActivity(), ChapterActivity.class);
        intent.putExtra(ChapterActivity.EXTRA_CHAPTER_ID, chapter.getChapterId());
        startActivity(intent);
    }

    public void setSeries(String seriesId) {
        mSeriesId = seriesId;
        reloadSeries();
    }

    private void reloadChapters() {
        reload(LOADER_ID_CHAPTERS, mChapterLoader);
    }

    private void reloadSeries() {
        reload(LOADER_ID_SERIES, mSeriesLoader);
    }

    @Override
    public void onPanelSlide(final float offset) {

    }

    private class ChapterListAdapter extends CollectionBaseAdapter<MangaChapter> {

        @Override
        public long getItemId(final int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final ChapterListItemView chapterView;
            if (convertView == null) {
                chapterView = (ChapterListItemView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.li_chapter, parent, false);
            } else {
                chapterView = (ChapterListItemView) convertView;
            }

            chapterView.populate(getItem(position));
            return chapterView;
        }
    }
}
