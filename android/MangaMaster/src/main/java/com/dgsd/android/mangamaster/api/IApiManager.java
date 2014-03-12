package com.dgsd.android.mangamaster.api;

import com.dgsd.android.mangamaster.model.SeriesListRequest;

/**
 * Responsible for making raw Api calls and persisting any results
 */
public interface IApiManager {

    public void getSeriesList(int limit, int offset, long updatedSince);

    public void getChaptersInSeries(String series, int limit, int offset, long updatedSince);

    public void getPagesInChapter(String series, int chapter);

    public void getPagesInChapter(String chapterId);
}