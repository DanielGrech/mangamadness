package com.dgsd.android.mangamaster.model;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;

/**
 * A single chapter in a series
 */
public class MangaChapter extends BaseModel {

    transient long mId;

    @SerializedName("id")
    String mChapterId;

    @SerializedName("series_id")
    String mSeriesId;

    @SerializedName("name")
    String mName;

    @SerializedName("title")
    String mTitle;

    @SerializedName("sequence_number")
    int mSequenceNumber;

    @SerializedName("release_date")
    long mReleaseDate;

    @SerializedName("time_created")
    long mTimeCreated;

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeLong(mId);
        dest.writeString(mChapterId);
        dest.writeString(mSeriesId);
        dest.writeString(mName);
        dest.writeString(mTitle);
        dest.writeInt(mSequenceNumber);
        dest.writeLong(mReleaseDate);
        dest.writeLong(mTimeCreated);
    }

    public static final Creator<MangaChapter> CREATOR = new Creator<MangaChapter>() {
        public MangaChapter createFromParcel(Parcel in) {
            MangaChapter c = new MangaChapter();

            c.mId = in.readLong();
            c.mChapterId = in.readString();
            c.mSeriesId = in.readString();
            c.mName = in.readString();
            c.mTitle = in.readString();
            c.mSequenceNumber = in.readInt();
            c.mReleaseDate = in.readLong();
            c.mTimeCreated = in.readLong();

            return c;
        }

        public MangaChapter[] newArray(int size) {
            return new MangaChapter[size];
        }
    };
}
