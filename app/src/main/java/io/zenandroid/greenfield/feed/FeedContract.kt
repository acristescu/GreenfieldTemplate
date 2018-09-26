package io.zenandroid.greenfield.feed

import android.graphics.Bitmap

import com.squareup.picasso.Target

import io.zenandroid.greenfield.model.Image

/**
 * Created by alex on 25/01/2018.
 */

interface FeedContract {
    enum class SortCriterion {
        PUBLISHED, TAKEN
    }

    interface View : io.zenandroid.greenfield.base.View<FeedContract.Presenter> {
        fun showImages(items: List<Image>)

        fun closeSearchBox()

        fun setSubTitle(subtitle: String)

        fun browseURL(link: String)

        fun loadImageFromURL(url: String, target: Target)

        fun saveBitmapToGallery(bitmap: Bitmap, title: String, description: String)

        fun sendEmail(url: String)
    }

    interface Presenter : io.zenandroid.greenfield.base.Presenter {

        fun onSearch(query: CharSequence)

        fun onSortBy(criterion: SortCriterion)

        fun onRefresh()

        fun onBrowseImage(image: Image)

        fun onShareImage(image: Image)

        fun onSaveImage(image: Image)
    }
}
