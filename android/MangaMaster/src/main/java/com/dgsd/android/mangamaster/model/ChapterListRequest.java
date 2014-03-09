package com.dgsd.android.mangamaster.model;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class ChapterListRequest extends BaseRequest {

    @SerializedName("series")
    String mSeries;

    @SerializedName("limit")
    int mLimit;

    @SerializedName("offset")
    int mOffset;

    @SerializedName("updated_since")
    long mUpdatedSince;

    @SerializedName("result")
    List<MangaChapter> mChapters;

    public String getSeries() {
        return mSeries;
    }

    public void setSeries(final String series) {
        mSeries = series;
    }

    public int getLimit() {
        return mLimit;
    }

    public void setLimit(final int limit) {
        mLimit = limit;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setOffset(final int offset) {
        mOffset = offset;
    }

    public long getUpdatedSince() {
        return mUpdatedSince;
    }

    public void setUpdatedSince(final long updatedSince) {
        mUpdatedSince = updatedSince;
    }

    public List<MangaChapter> getChapters() {
        return mChapters;
    }

    public void setChapters(final List<MangaChapter> chapters) {
        mChapters = chapters;
    }

    public boolean hasChapters() {
        return mChapters != null && !mChapters.isEmpty();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mSeries);
        dest.writeInt(mLimit);
        dest.writeInt(mOffset);
        dest.writeLong(mUpdatedSince);
        dest.writeTypedList(mChapters);
    }

    public static final Creator<ChapterListRequest> CREATOR = new Creator<ChapterListRequest>() {
        public ChapterListRequest createFromParcel(Parcel in) {
            final ChapterListRequest clr = new ChapterListRequest();

            clr.mSeries = in.readString();
            clr.mLimit = in.readInt();
            clr.mOffset = in.readInt();
            clr.mUpdatedSince = in.readLong();

            List<MangaChapter> chapters = new LinkedList<>();
            in.readTypedList(chapters, MangaChapter.CREATOR);
            if (!chapters.isEmpty()) {
                clr.mChapters = chapters;
            }

            return clr;
        }

        public ChapterListRequest[] newArray(int size) {
            return new ChapterListRequest[size];
        }
    };

    @Override
    public String toString() {
        return "ChapterListRequest{" +
                "mSeries=" + mSeries +
                "mLimit=" + mLimit +
                ", mOffset=" + mOffset +
                ", mUpdatedSince=" + mUpdatedSince +
                ", mChapters=" + mChapters +
                '}';
    }
}
