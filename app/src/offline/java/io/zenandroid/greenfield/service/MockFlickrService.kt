package io.zenandroid.greenfield.service

import androidx.annotation.VisibleForTesting
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.zenandroid.greenfield.model.ImageListResponse
import io.zenandroid.greenfield.util.EspressoIdlingResource
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * created by acristescu
 */

class MockFlickrService @Inject constructor() : FlickrService {

    private var mockResponse = ImageListResponse()
    private var delay = 0

    init {
        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        mockResponse = gson.fromJson(
                InputStreamReader(javaClass.classLoader!!.getResourceAsStream("mock_data.json")),
                ImageListResponse::class.java
        )
    }

    override fun getImageList(tags: String?): Single<ImageListResponse> {
        EspressoIdlingResource.increment()
        return Single.just(mockResponse)
                .delay(delay.toLong(), TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { EspressoIdlingResource.decrement() }
    }

    @VisibleForTesting
    fun setDelay(delay: Int) {
        this.delay = delay
    }
}
