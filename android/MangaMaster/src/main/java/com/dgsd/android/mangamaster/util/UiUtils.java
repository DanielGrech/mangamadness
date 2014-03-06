package com.dgsd.android.mangamaster.util;

import android.content.Context;
import android.content.res.Configuration;
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
}
