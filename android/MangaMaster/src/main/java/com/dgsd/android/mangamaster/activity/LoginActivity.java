package com.dgsd.android.mangamaster.activity;

import android.os.Bundle;
import android.widget.ViewFlipper;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.fragment.LoginChoiceFragment;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.view_flipper)
    ViewFlipper mFlipper;

    LoginChoiceFragment mChoiceFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        ButterKnife.inject(this);

        setupTintManagerForViews(false, true, mFlipper);
        setupViews();

        mChoiceFragment = findFragment(R.id.login_choice_fragment);
    }

    private void setupViews() {
        mFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        mFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
    }
}
