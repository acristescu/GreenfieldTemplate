package io.zenandroid.greenfield.service

import io.reactivex.Single
import io.zenandroid.greenfield.model.ImageListResponse

/**
 * created by acristescu
 */

interface FlickrService {
    fun getImageList(tags: String?): Single<ImageListResponse>
}
