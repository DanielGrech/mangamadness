package com.dgsd.android.mangamaster.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.dgsd.android.mangamaster.R;
import timber.log.Timber;

public class PhotoViewPager extends ViewPager {

    public PhotoViewPager(Context context) {
        super(context);
    }

    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    {
        setPageMargin(getResources().getDimensionPixelSize(R.dimen.photo_view_pager_page_margin));
        setPageTransformer(true, new PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                if (position < 0 || position >= 1.f) {
                    page.setTranslationX(0);
                    page.setAlpha(1.f);
                    page.setScaleX(1);
                    page.setScaleY(1);
                } else {
                    page.setTranslationX(-position * page.getWidth());
                    page.setAlpha(Math.max(0,1.f - position));
                    final float scale = Math.max(0, 1.f - position * 0.3f);
                    page.setScaleX(scale);
                    page.setScaleY(scale);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Timber.e(e, "Error in onInterceptTouchEvent()");
            return false;
        }
    }
}
