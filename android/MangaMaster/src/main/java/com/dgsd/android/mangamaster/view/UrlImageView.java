package com.dgsd.android.mangamaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.dgsd.android.mangamaster.util.UiUtils;
import com.koushikdutta.ion.Ion;

/**
 *
 */
public class UrlImageView extends ImageView {

    public UrlImageView(final Context context) {
        super(context);
    }

    public UrlImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public UrlImageView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(final String url) {
        if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
            UiUtils.onPreDraw(this, new Runnable() {
                @Override
                public void run() {
                    setImageUrl(url);
                }
            });
        } else {
            Ion.with(this)
                    .resize(getMeasuredWidth(), getMeasuredHeight())
                    .centerCrop()
                    .load(url);
        }
    }
}
