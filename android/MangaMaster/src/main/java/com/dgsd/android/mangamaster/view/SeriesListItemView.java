package com.dgsd.android.mangamaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.model.MangaSeries;

/**
 *
 */
public class SeriesListItemView extends RelativeLayout {

    @InjectView(R.id.primary)
    TextView mPrimary;

    public SeriesListItemView(final Context context) {
        super(context);
    }

    public SeriesListItemView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SeriesListItemView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void populate(MangaSeries series) {
        mPrimary.setText(series.getName());
    }
}
