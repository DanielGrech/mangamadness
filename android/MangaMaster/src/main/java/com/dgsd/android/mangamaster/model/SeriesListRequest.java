package com.dgsd.android.mangamaster.model;

import android.os.Parcel;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class SeriesListRequest extends BaseRequest {

    @SerializedName("limit")
    int mLimit;

    @SerializedName("offset")
    int mOffset;

    @SerializedName("updated_since")
    long mUpdatedSince;

    @SerializedName("result")
    List<MangaSeries> mSeries;

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

    public List<MangaSeries> getSeries() {
        return mSeries;
    }

    public void setSeries(final List<MangaSeries> series) {
        mSeries = series;
    }

    public boolean hasSeries() {
        return mSeries != null && !mSeries.isEmpty();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(mLimit);
        dest.writeInt(mOffset);
        dest.writeLong(mUpdatedSince);
        dest.writeTypedList(mSeries);
    }

    public static final Creator<SeriesListRequest> CREATOR = new Creator<SeriesListRequest>() {
        public SeriesListRequest createFromParcel(Parcel in) {
            final SeriesListRequest slr = new SeriesListRequest();

            slr.mLimit = in.readInt();
            slr.mOffset = in.readInt();
            slr.mUpdatedSince = in.readLong();

            List<MangaSeries> series = new LinkedList<>();
            in.readTypedList(series, MangaSeries.CREATOR);
            if (!series.isEmpty()) {
                slr.mSeries = series;
            }

            return slr;
        }

        public SeriesListRequest[] newArray(int size) {
            return new SeriesListRequest[size];
        }
    };

    @Override
    public String toString() {
        return "SeriesListRequest{" +
                "mLimit=" + mLimit +
                ", mOffset=" + mOffset +
                ", mUpdatedSince=" + mUpdatedSince +
                ", mSeries=" + mSeries +
                '}';
    }
}
