package com.dgsd.android.mangamaster.api;

import com.dgsd.android.mangamaster.model.*;

import java.util.List;

/**
 * Responsible for persisting the results of any API calls
 */
public interface IPersistenceManager {

    public void saveSeries(List<MangaSeries> series);

    public void saveChapters(List<MangaChapter> chapters);

    public void savePages(List<MangaPage> pages);
}
