package io.zenandroid.greenfield.service;

import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.zenandroid.greenfield.api.FlickrApi;
import io.zenandroid.greenfield.model.ImageListResponse;
import io.zenandroid.greenfield.util.EspressoIdlingResource;

public class FlickrServiceImpl implements FlickrService {

	private final static String TAG = FlickrServiceImpl.class.getSimpleName();

	private final FlickrApi flickrApi;

	@Inject
	public FlickrServiceImpl(FlickrApi api) {
		flickrApi = api;
	}

	@Override
	public Single<ImageListResponse> getImageList(@Nullable String tags) {
		EspressoIdlingResource.getInstance().increment();
		return flickrApi.getPhotos(tags)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.doFinally(() -> EspressoIdlingResource.getInstance().decrement());
	}
}
