package com.dgsd.android.mangamaster.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.fragment.AppDrawerFragment;
import com.dgsd.android.mangamaster.fragment.FavouritesFragment;
import com.dgsd.android.mangamaster.fragment.SeriesListFragment;
import com.squareup.otto.Subscribe;

/**
 *
 */
public class MainActivity extends BaseActivity {

    private static final int STATE_NONE = 0;
    private static final int STATE_FAVOURITES = 1;
    private static final int STATE_ALL = 2;
    private static final int STATE_LATEST = 3;

    private AppDrawerFragment mAppDrawerFragment;

    @InjectView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;

    ActionBarDrawerToggle mDrawerToggle;

    private StateMachine mStateMachine;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        ButterKnife.inject(this);

        setupDrawerToggle();
        setupNavigationDrawer();
        setupViews();
        setupTintManagerForViews(true, false, ButterKnife.findById(this, R.id.drawer),
                ButterKnife.findById(this, R.id.fragment_container));

        mStateMachine = new StateMachine();
        getFragmentManager().addOnBackStackChangedListener(mStateMachine);

        if (savedInstanceState == null) {
            mStateMachine.transitionTo(STATE_FAVOURITES);
        }
    }


    @Override
    protected void onJobRequestStart(final String action) {
//        if (!mFavouritesFragment.onJobRequestStart(action)) {
            // It doesn't belong to any of our fragments .. let's handle it ourselves

//        }
    }

    @Override
    protected void onJobRequestFinish(final String action) {
//        if (!mFavouritesFragment.onJobRequestStart(action)) {
            // It doesn't belong to any of our fragments .. let's handle it ourselves
//        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getFragmentManager().removeOnBackStackChangedListener(mStateMachine);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.act_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            switch (item.getItemId()) {
                case R.id.search:
                    //TODO: Search/Filter!
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onAppDrawerMenuItemPressed(AppDrawerFragment.OnAppDrawerItemClickEvent event) {
        switch (event.item) {
            case LATEST:
                mStateMachine.transitionTo(STATE_LATEST);
                break;
            case ALL_SERIES:
                mStateMachine.transitionTo(STATE_ALL);
                break;
            case FAVOURITES:
                mStateMachine.transitionTo(STATE_FAVOURITES);
                break;
            case DOWNLOADS:
                //TODO: Downloads!
                break;
        }

        mNavigationDrawer.closeDrawers();
    }

    private void setupDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mNavigationDrawer,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
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
        mAppDrawerFragment = findFragment(R.id.drawer);
    }

    private class StateMachine implements FragmentManager.OnBackStackChangedListener {
        private int mCurrentState = STATE_NONE;

        public void transitionTo(int state) {
            if (mCurrentState == state) {
                return;
            }

            setTitleForState(state);

            final String stateName = String.valueOf(state);

            final FragmentManager fm = getFragmentManager();

            if (mCurrentState != STATE_NONE && fm.popBackStackImmediate(stateName, 0)) {
                // We've in our new state
                mCurrentState = state;
                return;
            }

            final Fragment fragment;
            switch (state) {
                case STATE_NONE:
                    throw new IllegalArgumentException("Cant move back to none state");
                case STATE_FAVOURITES:
                    fragment = new FavouritesFragment();
                    break;
                case STATE_ALL:
                    fragment = SeriesListFragment.create(SeriesListFragment.DisplayType.ALPHA);
                    break;
                case STATE_LATEST:
                    fragment = SeriesListFragment.create(SeriesListFragment.DisplayType.LATEST);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized state: " + state);
            }

            final FragmentTransaction trans = fm.beginTransaction();
            trans.replace(R.id.fragment_container, fragment, stateName);
            if (state > STATE_FAVOURITES) {
                trans.addToBackStack(stateName);
            } else {
                mCurrentState = state;
            }

            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            trans.commit();


        }

        public int getState() {
            return mCurrentState;
        }

        private void setTitleForState(int state) {
            final int abTitleRes;

            switch (state) {
                case STATE_FAVOURITES:
                    abTitleRes = R.string.favourite_manga;
                    break;
                case STATE_ALL:
                    abTitleRes = R.string.all_manga;
                    break;
                case STATE_LATEST:
                    abTitleRes = R.string.latest_manga;
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized state: " + state);
            }

            getActionBar().setTitle(abTitleRes);
        }

        @Override
        public void onBackStackChanged() {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment != null) {
                String tag = fragment.getTag();
                if (tag == null || !TextUtils.isDigitsOnly(tag)) {
                    throw new IllegalStateException("Unexpected fragment tag: " + tag);
                } else {
                    mCurrentState = Integer.valueOf(tag);
                    setTitleForState(mCurrentState);
                }
            }
        }
    }
}
