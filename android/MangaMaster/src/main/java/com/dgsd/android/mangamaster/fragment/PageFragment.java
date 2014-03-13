package com.dgsd.android.mangamaster.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.model.MangaPage;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 *
 */
public class PageFragment extends BaseFragment implements PhotoViewAttacher.OnViewTapListener {

    private static final String KEY_PAGE = "_page";

    private static DecelerateInterpolator sDecelerateInterpolator = new DecelerateInterpolator(1.5f);

    @InjectView(R.id.image)
    PhotoView mImage;

    @InjectView(R.id.progress)
    ProgressBar mProgressBar;

    private MangaPage mPage;

    public static PageFragment create(MangaPage page) {
        if (page == null) {
            throw new IllegalArgumentException("page == null");
        }

        PageFragment frag = new PageFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_PAGE, page);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPage = getArguments() == null ?
                null : (MangaPage) getArguments().getParcelable(KEY_PAGE);

        if (mPage == null) {
            throw new IllegalStateException("Didnt get passed a page to display");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.frag_page, container, false);
        ButterKnife.inject(this, v);

        mImage.setMaximumScale(8f);
        mImage.setOnViewTapListener(this);

        showProgressBar();

        Ion.with(getActivity())
                .load(mPage.getImageUrl())
                .withBitmap()
                .deepZoom()
//                .placeholder(R.drawable.page_placeholder)
//                .error(R.drawable.page_error)
                .intoImageView(mImage)
                .setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(final Exception e, final ImageView imageView) {
                        hideProgressBar();
                    }
                });

        return v;
    }

    @Override
    public void onViewTap(final View view, final float v, final float v2) {
        mEventBus.post(new PageClickEvent());
    }

    private void showProgressBar() {
        if (mProgressBar == null) {
            return;
        }

        mProgressBar.setScaleX(0f);
        mProgressBar.setScaleY(0f);
        mProgressBar.setAlpha(0f);

        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.animate()
                .scaleY(1f)
                .scaleX(1f)
                .alpha(1f)
                .setInterpolator(sDecelerateInterpolator);
    }

    private void hideProgressBar() {
        if (mProgressBar == null) {
            return;
        }

        mProgressBar.animate()
                .scaleY(0)
                .scaleX(0)
                .alpha(0)
                .setInterpolator(sDecelerateInterpolator)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public static class PageClickEvent {
    }
}
