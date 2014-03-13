package com.dgsd.android.mangamaster.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.data.ChapterLoader;
import com.dgsd.android.mangamaster.data.PageLoader;
import com.dgsd.android.mangamaster.fragment.PageFragment;
import com.dgsd.android.mangamaster.jobs.GetPagesListJob;
import com.dgsd.android.mangamaster.model.MangaChapter;
import com.dgsd.android.mangamaster.model.MangaPage;
import com.dgsd.android.mangamaster.util.Api;
import com.dgsd.android.mangamaster.view.FragmentStatePagerAdapter;
import com.dgsd.android.mangamaster.view.PhotoViewPager;
import com.path.android.jobqueue.JobManager;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import java.util.List;

/**
 *
 */
public class ChapterActivity extends BaseActivity {

    public static final String EXTRA_CHAPTER_ID = "_chapter_id";

    private static final int LOADER_ID_CHAPTER = 1;
    private static final int LOADER_ID_PAGES = 2;

    private String mChapterId;

    @InjectView(R.id.container)
    ViewGroup mContainer;

    @InjectView(R.id.pager)
    PhotoViewPager mViewPager;

    @Inject
    JobManager mJobManager;

    private MangaPageAdapter mAdapter;

    private MangaChapter mChapter;

    private boolean mHasMadeInitialApiRequest;

    private LoaderManager.LoaderCallbacks<List<MangaPage>> mPageLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<List<MangaPage>>() {
        @Override
        public Loader<List<MangaPage>> onCreateLoader(final int id, final Bundle args) {
            return new PageLoader(ChapterActivity.this, mChapterId);
        }

        @Override
        public void onLoadFinished(final Loader<List<MangaPage>> loader, final List<MangaPage> pages) {
            mAdapter.setPages(pages);
        }

        @Override
        public void onLoaderReset(final Loader<List<MangaPage>> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<List<MangaChapter>> mChapterLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<List<MangaChapter>>() {
        @Override
        public Loader<List<MangaChapter>> onCreateLoader(final int id, final Bundle args) {
            return ChapterLoader.createWithChapterId(ChapterActivity.this, mChapterId);
        }

        @Override
        public void onLoadFinished(final Loader<List<MangaChapter>> loader, final List<MangaChapter> chapters) {
            mChapter = chapters == null || chapters.size() != 1 ? null : chapters.get(0);
            if (mChapter != null) {
                updateDisplayForChapter();
                if (!mHasMadeInitialApiRequest) {
                    mJobManager.addJobInBackground(new GetPagesListJob(mChapter.getChapterId()));
                    mHasMadeInitialApiRequest = true;
                }
            }
        }

        @Override
        public void onLoaderReset(final Loader<List<MangaChapter>> loader) {

        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chapter);
        ButterKnife.inject(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new MangaPageAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(4); //TODO: Adjust based on network connection...

        mChapterId = getIntent().getStringExtra(EXTRA_CHAPTER_ID);
        if (TextUtils.isEmpty(mChapterId)) {
            throw new IllegalStateException("Need to pass a chapter id!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);
        reload(LOADER_ID_PAGES, mPageLoaderCallbacks);
        reload(LOADER_ID_CHAPTER, mChapterLoaderCallbacks);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    private void updateDisplayForChapter() {
        if (mChapter != null) {
            getActionBar().setTitle(mChapter.getName());
            getActionBar().setSubtitle(TextUtils.isEmpty(mChapter.getTitle()) ?
                    null : mChapter.getTitle());
        }
    }

    @Subscribe
    public void onPageClickEvent(PageFragment.PageClickEvent event) {
        showHideChrome((mContainer.getSystemUiVisibility()
                & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0);
    }

    private void showHideChrome(boolean show) {
        int flags = show ? 0 : View.SYSTEM_UI_FLAG_LOW_PROFILE;

        if (Api.isMin(Api.KITKAT)) {
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (!show) {
                flags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
            }
        }

        mContainer.setSystemUiVisibility(flags);
    }

    private class MangaPageAdapter extends FragmentStatePagerAdapter {

        private List<MangaPage> mPages;

        public MangaPageAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            return PageFragment.create(mPages.get(position));
        }

        @Override
        public int getCount() {
            return mPages == null ? 0 : mPages.size();
        }

        public void setPages(List<MangaPage> pages) {
            mPages = pages;
            notifyDataSetChanged();
        }
    }
}
