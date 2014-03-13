package com.dgsd.android.mangamaster.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *
 */
public class FavouritesFragment extends BaseFragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final TextView tv = new TextView(getActivity());
        tv.setText("Favourites go here");
        return tv;
    }
}
