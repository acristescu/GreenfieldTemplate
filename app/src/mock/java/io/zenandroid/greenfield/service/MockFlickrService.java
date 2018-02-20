package io.zenandroid.greenfield.service;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.zenandroid.greenfield.model.ImageListResponse;
import io.zenandroid.greenfield.util.EspressoIdlingResource;

/**
 * created by acristescu
 */

public class MockFlickrService implements FlickrService {

	private ImageListResponse mockResponse = new ImageListResponse();
	private int delay = 0;

	@Inject public MockFlickrService() {
		final Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.create();
		mockResponse = gson.fromJson(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("mock_data.json")),
				ImageListResponse.class
		);
	}

	@Override
	public Single<ImageListResponse> getImageList(@Nullable String tags) {
		EspressoIdlingResource.getInstance().increment();
		return Single.just(mockResponse)
				.delay(delay, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.doFinally(() -> EspressoIdlingResource.getInstance().decrement());
	}

	@VisibleForTesting
	public void setDelay(int delay) {
		this.delay = delay;
	}
}
