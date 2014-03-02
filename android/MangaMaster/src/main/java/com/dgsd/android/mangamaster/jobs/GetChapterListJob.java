package com.dgsd.android.mangamaster.jobs;

import com.path.android.jobqueue.Params;

/**
 *
 */
public class GetChapterListJob extends BaseJob {

    private final String mSeries;
    private final int mLimit;
    private final int mOffset;
    private final long mUpdatedSince;

    public GetChapterListJob(String series, int limit, int offset, long updatedSince) {
        super(new Params(PRIORITY_DEFAULT)
                .groupBy(GetChapterListJob.class.getSimpleName() + series));

        mSeries = series;
        mLimit = limit;
        mOffset = offset;
        mUpdatedSince = updatedSince;
    }

    @Override
    protected void runJob() {
        mApiManager.getChaptersInSeries(mSeries, mLimit, mOffset, mUpdatedSince);
    }
}
