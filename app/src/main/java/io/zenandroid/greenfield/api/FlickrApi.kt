package io.zenandroid.greenfield.api

import io.reactivex.Single
import io.zenandroid.greenfield.model.ImageListResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * created by acristescu
 */

interface FlickrApi {

    @GET("services/feeds/photos_public.gne?format=json&nojsoncallback=1")
    fun getPhotos(@Query("tags") tags: String?): Single<ImageListResponse>
}
