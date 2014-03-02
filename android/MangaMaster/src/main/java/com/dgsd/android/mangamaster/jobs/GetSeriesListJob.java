package com.dgsd.android.mangamaster.jobs;

import com.path.android.jobqueue.Params;

/**
 *
 */
public class GetSeriesListJob extends BaseJob {

    private final int mLimit;
    private final int mOffset;
    private final long mUpdatedSince;

    public GetSeriesListJob(int limit, int offset, long updatedSince) {
        super(new Params(PRIORITY_DEFAULT)
                .groupBy(GetSeriesListJob.class.getSimpleName()));

        mLimit = limit;
        mOffset = offset;
        mUpdatedSince = updatedSince;
    }

    @Override
    protected void runJob() {
        mApiManager.getSeriesList(mLimit, mOffset, mUpdatedSince);
    }
}
