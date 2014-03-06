package com.dgsd.android.mangamaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.*;
import android.widget.ListView;

/**
 *
 */
public class FadeInListView extends ListView {
    public FadeInListView(final Context context) {
        super(context);
    }

    public FadeInListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public FadeInListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    {
        final AlphaAnimation alpha = new AlphaAnimation(0.2f, 1f);
        alpha.setInterpolator(new DecelerateInterpolator());
        alpha.setDuration(100);

        setLayoutAnimation(new LayoutAnimationController(alpha));
    }
}
