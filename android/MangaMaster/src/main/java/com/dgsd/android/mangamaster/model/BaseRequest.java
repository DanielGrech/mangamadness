package com.dgsd.android.mangamaster.model;

import android.os.Parcelable;

/**
 * Base class for all the models in the app
 */
public abstract class BaseRequest implements Parcelable {

    @Override
    public int describeContents() {
        return hashCode();
    }
}
