package com.dgsd.android.mangamaster.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.util.Api;

/**
 *
 */
public class ChapterActivity extends BaseActivity {

    public static final String EXTRA_CHAPTER_ID = "_chapter_id";

    private String mChapterId;

    @InjectView(R.id.container)
    ViewGroup mContainer;

    private GestureDetector mGestureDetector;

    private GestureDetector.SimpleOnGestureListener mTapGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(final MotionEvent e) {
            showHideChrome((mContainer.getSystemUiVisibility()
                    & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0);
            return false;
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chapter);
        ButterKnife.inject(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mChapterId = getIntent().getStringExtra(EXTRA_CHAPTER_ID);
        if (TextUtils.isEmpty(mChapterId)) {
            throw new IllegalStateException("Need to pass a chapter id!");
        }

        mGestureDetector = new GestureDetector(this, mTapGestureListener);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
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
}
