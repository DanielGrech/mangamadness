package com.dgsd.android.mangamaster.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.util.EnumUtils;
import com.path.android.jobqueue.JobManager;

import javax.inject.Inject;

/**
 *
 */
public class MainActivity extends BaseActivity {

    private static enum Page {
        ALPHA, LATEST, FAVOURITES
    }

    @InjectView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;

    @InjectView(R.id.view_pager)
    ViewPager mPager;

    @Inject
    JobManager mJobManager;

    ActionBarDrawerToggle mDrawerToggle;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        ButterKnife.inject(this);

        setupDrawerToggle();
        setupNavigationDrawer();
        setupViews();
        setupTintManagerForViews(true, false, ButterKnife.findById(this, R.id.drawer),
                ButterKnife.findById(this, R.id.view_pager));
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mNavigationDrawer,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }
        };

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    private void setupNavigationDrawer() {
        mNavigationDrawer.setDrawerListener(mDrawerToggle);
        mNavigationDrawer.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
    }

    private void setupViews() {
        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.main_pager_page_margin));
        mPager.setAdapter(new PageAdapter());
    }

    private class PageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Page.values().length;
        }

        @Override
        public boolean isViewFromObject(final View view, final Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            final TextView tv = new TextView(container.getContext());
            tv.setText(String.valueOf(position));

            container.addView(tv);

            return tv;
        }

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (EnumUtils.from(Page.class, position)) {
                case ALPHA:
                    return getString(R.string.all);
                case LATEST:
                    return getString(R.string.latest);
                case FAVOURITES:
                    return getString(R.string.favourites);
                default:
                    throw new IllegalStateException("Unexpected pager position: " + position);
            }
        }
    }
}
