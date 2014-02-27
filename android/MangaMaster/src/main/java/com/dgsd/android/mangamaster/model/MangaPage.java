package com.dgsd.android.mangamaster.model;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;

/**
 * A single page of a chapter
 */
public class MangaPage extends BaseModel {

    transient long mId;

    @SerializedName("id")
    String mPageId;

    @SerializedName("chapter_id")
    String mChapterId;

    @SerializedName("image_url")
    String mImageUrl;

    @SerializedName("name")
    int mName;

    @SerializedName("time_created")
    long mTimeCreated;

    public long getId() {
        return mId;
    }

    public void setId(final long id) {
        mId = id;
    }

    public String getPageId() {
        return mPageId;
    }

    public void setPageId(final String pageId) {
        mPageId = pageId;
    }

    public String getChapterId() {
        return mChapterId;
    }

    public void setChapterId(final String chapterId) {
        mChapterId = chapterId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        mImageUrl = imageUrl;
    }

    public int getName() {
        return mName;
    }

    public void setName(final int name) {
        mName = name;
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
        dest.writeString(mPageId);
        dest.writeString(mChapterId);
        dest.writeString(mImageUrl);
        dest.writeInt(mName);
        dest.writeLong(mTimeCreated);
    }

    public static final Creator<MangaPage> CREATOR = new Creator<MangaPage>() {
        public MangaPage createFromParcel(Parcel in) {
            MangaPage p = new MangaPage();

            p.mId = in.readLong();
            p.mPageId = in.readString();
            p.mChapterId = in.readString();
            p.mImageUrl = in.readString();
            p.mName = in.readInt();
            p.mTimeCreated = in.readLong();

            return p;
        }

        public MangaPage[] newArray(int size) {
            return new MangaPage[size];
        }
    };
}
