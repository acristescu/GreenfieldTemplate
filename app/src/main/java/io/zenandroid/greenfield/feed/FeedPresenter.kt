package io.zenandroid.greenfield.feed

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Single
import io.zenandroid.greenfield.base.BasePresenter
import io.zenandroid.greenfield.model.Image
import io.zenandroid.greenfield.model.ImageListResponse
import io.zenandroid.greenfield.service.FlickrService

/**
 * Created by alex on 25/01/2018.
 */

class FeedPresenter(private val view: FeedContract.View, private val flickrService: FlickrService) : BasePresenter(view), FeedContract.Presenter {
    private var criterion: FeedContract.SortCriterion = FeedContract.SortCriterion.PUBLISHED
    private var tags: String? = null
    private var imageListResponse: ImageListResponse? = null

    override fun subscribe() {
        fetchImages(null)
    }

    private fun setImageList(imageListResponse: ImageListResponse) {
        view.dismissProgressDialog()
        imageListResponse.items?.let(view::showImages)
    }

    override fun onSearch(query: CharSequence) {
        view.closeSearchBox()
        view.setSubTitle(query.toString())
        unsubscribe() // make sure we're cancelling any previous searches
        imageListResponse = null
        tags = query.toString().replace("\\s+".toRegex(), ",")
        fetchImages(tags)
    }

    override fun onSortBy(criterion: FeedContract.SortCriterion) {
        if (criterion == this.criterion) {
            return
        }

        unsubscribe()
        this.criterion = criterion
        fetchImages(tags)
    }

    override fun onRefresh() {
        imageListResponse = null
        fetchImages(tags)
    }

    override fun onBrowseImage(image: Image) {
        image.link?.let(view::browseURL)
    }

    override fun onShareImage(image: Image) {
        image.media?.m?.let(view::sendEmail)
    }

    override fun onSaveImage(image: Image) {
        image.media?.m?.let {
            view.loadImageFromURL(it, object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    view.saveBitmapToGallery(bitmap,
                            image.title ?: "Default Title",
                            image.tags ?: "Default Description")
                }

                override fun onBitmapFailed(errorDrawable: Drawable) {}

                override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
            })
        }
    }

    private fun fetchImages(tags: String?) {
        view.showProgressDialog()
        val source = imageListResponse?.let { Single.just(it) }
                ?: flickrService.getImageList(tags)

        addDisposable(source
                .doOnSuccess(this::storeList)
                .doOnSuccess(this::sortList)
                .subscribe(this::setImageList, this::onError)
        )
    }

    private fun sortList(imageListResponse: ImageListResponse) {
        imageListResponse.items = imageListResponse.items?.sortedBy {
            when (criterion) {
                FeedContract.SortCriterion.TAKEN -> it.dateTaken
                FeedContract.SortCriterion.PUBLISHED -> it.published
            }
        }
    }

    private fun storeList(imageListResponse: ImageListResponse) {
        this.imageListResponse = imageListResponse
    }
}
