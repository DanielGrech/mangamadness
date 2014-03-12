package com.dgsd.android.mangamaster.api;

import com.dgsd.android.mangamaster.model.ChapterListRequest;
import com.dgsd.android.mangamaster.model.PageListRequest;
import com.dgsd.android.mangamaster.model.SeriesListRequest;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 *
 */
public interface MangaMadnessApi {

    @GET("/series/")
    public SeriesListRequest getSeriesList(@Query("limit") int limit,
                                           @Query("offset") int offset,
                                           @Query("updated_since") long updatedSince);

    @GET("/series/{series}/chapters")
    public ChapterListRequest getChapterList(@Path("series") String series,
                                             @Query("limit") int limit,
                                             @Query("offset") int offset,
                                             @Query("updated_since") long updatedSince);

    @GET("/series/{series}/chapters/{chapter}")
    public PageListRequest getPages(@Path("series") String series,
                                    @Path("chapter") int chapter);

    @GET("/chapters/{chapter}")
    public PageListRequest getPages(@Path("chapter") String chapterId);
}
