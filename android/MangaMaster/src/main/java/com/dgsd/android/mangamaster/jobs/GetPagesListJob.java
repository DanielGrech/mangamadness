package com.dgsd.android.mangamaster.jobs;

import com.path.android.jobqueue.Params;

/**
 *
 */
public class GetPagesListJob extends BaseJob {

    private final String mSeries;
    private final int mChapter;

    public GetPagesListJob(String series, int chapter) {
        super(new Params(PRIORITY_DEFAULT)
                .groupBy(GetPagesListJob.class.getSimpleName() + series + chapter));

        mSeries = series;
        mChapter = chapter;
    }

    @Override
    protected void runJob() {
        mApiManager.getPagesInChapter(mSeries, mChapter);
    }
}
