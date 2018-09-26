package io.zenandroid.greenfield.service

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.zenandroid.greenfield.api.FlickrApi
import io.zenandroid.greenfield.model.ImageListResponse
import io.zenandroid.greenfield.util.EspressoIdlingResource
import javax.inject.Inject

class FlickrServiceImpl @Inject
constructor(private val flickrApi: FlickrApi) : FlickrService {

    override fun getImageList(tags: String?): Single<ImageListResponse> {
        EspressoIdlingResource.increment()
        return flickrApi.getPhotos(tags)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { EspressoIdlingResource.decrement() }
    }

    companion object {

        private val TAG = FlickrServiceImpl::class.java.simpleName
    }
}
