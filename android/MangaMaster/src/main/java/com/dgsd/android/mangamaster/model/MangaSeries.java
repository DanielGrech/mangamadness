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

    public long getId() {
        return mId;
    }

    public void setId(final long id) {
        mId = id;
    }

    public String getSeriesId() {
        return mSeriesId;
    }

    public void setSeriesId(final String seriesId) {
        mSeriesId = seriesId;
    }

    public String getName() {
        return mName;
    }

    public void setName(final String name) {
        mName = name;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(final String author) {
        mAuthor = author;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(final String artist) {
        mArtist = artist;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(final String summary) {
        mSummary = summary;
    }

    public String getUrlSegment() {
        return mUrlSegment;
    }

    public void setUrlSegment(final String urlSegment) {
        mUrlSegment = urlSegment;
    }

    public String getCoverImageUrl() {
        return mCoverImageUrl;
    }

    public void setCoverImageUrl(final String coverImageUrl) {
        mCoverImageUrl = coverImageUrl;
    }

    public int getYearOfRelease() {
        return mYearOfRelease;
    }

    public void setYearOfRelease(final int yearOfRelease) {
        mYearOfRelease = yearOfRelease;
    }

    public List<String> getGenres() {
        return mGenres;
    }

    public void setGenres(final List<String> genres) {
        mGenres = genres;
    }

    public boolean hasGenres() {
        return mGenres != null && !mGenres.isEmpty();
    }

    public long getTimeCreated() {
        return mTimeCreated;
    }

    public void setTimeCreated(final long timeCreated) {
        mTimeCreated = timeCreated;
    }

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

    @Override
    public String toString() {
        return "MangaSeries{" +
                "mId=" + mId +
                ", mSeriesId='" + mSeriesId + '\'' +
                ", mName='" + mName + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mArtist='" + mArtist + '\'' +
                ", mSummary='" + mSummary + '\'' +
                ", mUrlSegment='" + mUrlSegment + '\'' +
                ", mCoverImageUrl='" + mCoverImageUrl + '\'' +
                ", mYearOfRelease=" + mYearOfRelease +
                ", mGenres=" + mGenres +
                ", mTimeCreated=" + mTimeCreated +
                '}';
    }
}
