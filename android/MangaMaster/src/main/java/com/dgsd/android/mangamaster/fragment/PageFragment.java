package com.dgsd.android.mangamaster.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.dgsd.android.mangamaster.R;
import com.dgsd.android.mangamaster.model.MangaPage;
import com.koushikdutta.ion.Ion;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 *
 */
public class PageFragment extends BaseFragment implements PhotoViewAttacher.OnViewTapListener {

    private static final String KEY_PAGE = "_page";

    @InjectView(R.id.image)
    PhotoView mImage;

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

        Ion.with(getActivity())
                .load(mPage.getImageUrl())
                .withBitmap()
//                .deepZoom()
                .intoImageView(mImage);

        return v;
    }

    @Override
    public void onViewTap(final View view, final float v, final float v2) {
        mEventBus.post(new PageClickEvent());
    }

    public static class PageClickEvent {}
}
