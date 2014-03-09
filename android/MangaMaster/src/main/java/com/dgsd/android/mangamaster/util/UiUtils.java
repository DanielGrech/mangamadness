package com.dgsd.android.mangamaster.util;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewTreeObserver;
import com.dgsd.android.mangamaster.R;

/**
 *
 */
public class UiUtils {

    public static boolean isPortrait(Context context) {
        return context.getResources().getBoolean(R.bool.is_portrait);
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.is_tablet);
    }

    public static void onPreDraw(final View v, final Runnable action) {
        v.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        v.getViewTreeObserver().removeOnPreDrawListener(this);
                        action.run();
                        return false;
                    }
                });
    }
}
