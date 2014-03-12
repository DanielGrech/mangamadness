package com.dgsd.android.mangamaster.jobs;

import com.path.android.jobqueue.Params;

/**
 *
 */
public class GetPagesListJob extends BaseJob {

    private final String mChapterId;

    public GetPagesListJob(String chapterId) {
        super(new Params(PRIORITY_DEFAULT)
                .groupBy(GetPagesListJob.class.getSimpleName() + chapterId));

        mChapterId = chapterId;
    }

    @Override
    protected void runJob() {
        mApiManager.getPagesInChapter(mChapterId);
    }
}
