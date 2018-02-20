package io.zenandroid.greenfield.service;

import android.support.annotation.Nullable;

import io.reactivex.Single;
import io.zenandroid.greenfield.model.ImageListResponse;

/**
 * created by acristescu
 */

public interface FlickrService {
	Single<ImageListResponse> getImageList(@Nullable String tags);
}
