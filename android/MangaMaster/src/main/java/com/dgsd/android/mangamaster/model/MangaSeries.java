package com.dgsd.android.mangamaster.model;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MangaSeries extends BaseModel {

    transient long mId;

    @SerializedName("id")
    String mSeriesId;

    @SerializedName("name")
    String mName;

    @SerializedName("author")
    String mAuthor;

    @SerializedName("artist")
    String mArtist;

    @SerializedName("summary")
    String mSummary;

    @SerializedName("url_segment")
    String mUrlSegment;

    @SerializedName("cover_image_url")
    String mCoverImageUrl;

    @SerializedName("year_of_release")
    int mYearOfRelease;

    @SerializedName("genres")
    List<String> mGenres;

    @SerializedName("time_created")
    long mTimeCreated;

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeLong(mId);
        dest.writeString(mSeriesId);
        dest.writeString(mName);
        dest.writeString(mAuthor);
        dest.writeString(mArtist);
        dest.writeString(mSummary);
        dest.writeString(mUrlSegment);
        dest.writeString(mCoverImageUrl);
        dest.writeInt(mYearOfRelease);
        dest.writeLong(mTimeCreated);
        dest.writeStringList(mGenres);
    }

    public static final Creator<MangaSeries> CREATOR = new Creator<MangaSeries>() {
        public MangaSeries createFromParcel(Parcel in) {
            final MangaSeries s = new MangaSeries();

            s.mId = in.readLong();
            s.mSeriesId = in.readString();
            s.mName = in.readString();
            s.mAuthor = in.readString();
            s.mArtist = in.readString();
            s.mSummary = in.readString();
            s.mUrlSegment = in.readString();
            s.mCoverImageUrl = in.readString();
            s.mYearOfRelease = in.readInt();
            s.mTimeCreated = in.readLong();

            List<String> genres = new ArrayList<>();
            in.readStringList(genres);
            s.mGenres = genres;

            return s;
        }

        public MangaSeries[] newArray(int size) {
            return new MangaSeries[size];
        }
    };
}
