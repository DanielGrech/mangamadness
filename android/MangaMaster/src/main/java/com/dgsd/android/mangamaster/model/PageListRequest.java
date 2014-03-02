package com.dgsd.android.mangamaster.model;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class PageListRequest extends BaseRequest {

    @SerializedName("series")
    String mSeries;

    @SerializedName("chapter")
    int mChapter;

    @SerializedName("result")
    List<MangaPage> mPages;

    public String getSeries() {
        return mSeries;
    }

    public void setSeries(final String series) {
        mSeries = series;
    }

    public int getChapter() {
        return mChapter;
    }

    public void setChapter(final int chapter) {
        mChapter = chapter;
    }

    public List<MangaPage> getPages() {
        return mPages;
    }

    public void setPages(final List<MangaPage> pages) {
        mPages = pages;
    }

    public boolean hasPages() {
        return mPages != null && !mPages.isEmpty();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mSeries);
        dest.writeInt(mChapter);
        dest.writeTypedList(mPages);
    }

    public static final Creator<PageListRequest> CREATOR = new Creator<PageListRequest>() {
        public PageListRequest createFromParcel(Parcel in) {
            final PageListRequest slr = new PageListRequest();

            slr.mSeries = in.readString();
            slr.mChapter = in.readInt();

            List<MangaPage> pages = new LinkedList<>();
            in.readTypedList(pages, MangaPage.CREATOR);
            if (!pages.isEmpty()) {
                slr.mPages = pages;
            }

            return slr;
        }

        public PageListRequest[] newArray(int size) {
            return new PageListRequest[size];
        }
    };

    @Override
    public String toString() {
        return "PageListRequest{" +
                "mSeries='" + mSeries + '\'' +
                ", mChapter=" + mChapter +
                ", mPages=" + mPages +
                '}';
    }
}
