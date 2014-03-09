package com.dgsd.android.mangamaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.model.MangaChapter;
import com.dgsd.android.mangamaster.model.MangaSeries;

/**
 *
 */
public class ChapterListItemView extends RelativeLayout {

    @InjectView(R.id.primary)
    TextView mPrimary;

    public ChapterListItemView(final Context context) {
        super(context);
    }

    public ChapterListItemView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ChapterListItemView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void populate(MangaChapter chapter) {
        mPrimary.setText(chapter.getName());
    }
}
