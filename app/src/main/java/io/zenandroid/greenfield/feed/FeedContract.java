package io.zenandroid.greenfield.feed;

import android.graphics.Bitmap;

import com.squareup.picasso.Target;

import java.util.List;

import io.zenandroid.greenfield.model.Image;

/**
 * Created by alex on 25/01/2018.
 */

public interface FeedContract {
    enum SortCriterion {
        PUBLISHED, TAKEN
    }

    interface View extends io.zenandroid.greenfield.base.View<FeedContract.Presenter> {
        void showImages(List<Image> items);

        void closeSearchBox();

        void setSubTitle(String subtitle);

        void browseURL(String link);

        void loadImageFromURL(String url, Target target);

        void saveBitmapToGallery(Bitmap bitmap, String title, String description);

        void sendEmail(String url);
    }

    interface Presenter extends io.zenandroid.greenfield.base.Presenter {

        void onSearch(CharSequence query);

        void onSortBy(SortCriterion criterion);

        void onRefresh();

        void onBrowseImage(Image image);

        void onShareImage(Image image);

        void onSaveImage(Image image);
    }
}
