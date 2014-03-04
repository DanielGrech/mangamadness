package com.dgsd.android.mangamaster.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.dgsd.android.mangamaster.R;

public class LoginChoiceFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.frag_login, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.sign_in_wrapper_google)
    public void onGoogleSignInButtonClicked() {

    }

    @OnClick(R.id.sign_in_wrapper_facebook)
    public void onFacebookSignInButtonClicked() {

    }
}
