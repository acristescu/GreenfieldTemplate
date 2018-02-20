package io.zenandroid.greenfield.feed;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Collections;

import io.reactivex.Single;
import io.zenandroid.greenfield.base.BasePresenter;
import io.zenandroid.greenfield.model.Image;
import io.zenandroid.greenfield.model.ImageListResponse;
import io.zenandroid.greenfield.service.FlickrService;

/**
 * Created by alex on 25/01/2018.
 */

public class FeedPresenter extends BasePresenter implements FeedContract.Presenter {

    private final FlickrService flickrService;
    private final FeedContract.View view;
    private FeedContract.SortCriterion criterion = FeedContract.SortCriterion.PUBLISHED;
    private String tags;
    private ImageListResponse imageListResponse;

    public FeedPresenter(FeedContract.View view, FlickrService flickrService) {
        super(view);

        this.view = view;
        this.flickrService = flickrService;
    }

    @Override
    public void subscribe() {
        fetchImages(null);
    }

    private void setImageList(ImageListResponse imageListResponse) {
        view.dismissProgressDialog();
        view.showImages(imageListResponse.getItems());
    }

    @Override
    public void onSearch(CharSequence query) {
        view.closeSearchBox();
        view.setSubTitle(query.toString());
        unsubscribe(); // make sure we're cancelling any previous searches
        imageListResponse = null;
        tags = query.toString().replaceAll("\\s+", ",");
        fetchImages(tags);
    }

    @Override
    public void onSortBy(FeedContract.SortCriterion criterion) {
        if(criterion == this.criterion) {
            return;
        }

        unsubscribe();
        this.criterion = criterion;
        fetchImages(tags);
    }

    @Override
    public void onRefresh() {
        imageListResponse = null;
        fetchImages(tags);
    }

    @Override
    public void onBrowseImage(Image image) {
        view.browseURL(image.getLink());
    }

    @Override
    public void onShareImage(Image image) {
        view.sendEmail(image.getMedia().getM());
    }

    @Override
    public void onSaveImage(Image image) {
        view.loadImageFromURL(image.getMedia().getM(), new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                view.saveBitmapToGallery(bitmap, image.getTitle(), image.getTags());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
    }

    private void fetchImages(@Nullable String tags) {
        view.showProgressDialog();
        final Single<ImageListResponse> source = imageListResponse != null ?
                Single.just(imageListResponse) :
                flickrService.getImageList(tags).doOnSuccess(this::storeList);
        addDisposable(source
                        .doOnSuccess(this::storeList)
                        .doOnSuccess(this::sortList)
                        .subscribe(this::setImageList, this::onError)
        );
    }

    private void sortList(ImageListResponse imageListResponse) {
        Collections.sort(imageListResponse.getItems(), (o1, o2) -> {
            switch (criterion) {
                case TAKEN:
                    if(o1.getDateTaken() == null) {
                        return -1;
                    }
                    return o1.getDateTaken().compareTo(o2.getDateTaken());
                case PUBLISHED:
                    if(o1.getPublishedDate() == null) {
                        return -1;
                    }
                    return o1.getPublishedDate().compareTo(o2.getPublishedDate());
                default:
                    return 0;
            }
        });
    }

    private void storeList(ImageListResponse imageListResponse) {
        this.imageListResponse = imageListResponse;
    }
}
