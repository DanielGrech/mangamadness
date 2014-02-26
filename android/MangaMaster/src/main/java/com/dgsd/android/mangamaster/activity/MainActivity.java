package com.dgsd.android.mangamaster.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.util.Api;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.otto.Subscribe;

/**
 *
 */
public class MainActivity extends BaseActivity {

    @InjectView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;

    ActionBarDrawerToggle mDrawerToggle;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        ButterKnife.inject(this);

        setupDrawerToggle();
        setupNavigationDrawer();
        setupTintManager();
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

    private void setupTintManager() {
        if (Api.isMin(Api.KITKAT)) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setTintColor(getResources().getColor(R.color.ab_color));
            tintManager.setStatusBarTintEnabled(true);

            //We want to insert our app drawer
            final View v = ButterKnife.findById(this, R.id.drawer);
            DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) v.getLayoutParams();
            layoutParams.topMargin = tintManager.getConfig().getPixelInsetTop(true);
            v.setLayoutParams(layoutParams);
        }
    }
}
