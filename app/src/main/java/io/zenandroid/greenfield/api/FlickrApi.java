package io.zenandroid.greenfield.api;

import android.support.annotation.Nullable;

import io.reactivex.Single;
import io.zenandroid.greenfield.model.ImageListResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * created by acristescu
 */

public interface FlickrApi {

	@GET("services/feeds/photos_public.gne?format=json&nojsoncallback=1")
	Single<ImageListResponse> getPhotos(@Nullable @Query("tags") String tags);
}
