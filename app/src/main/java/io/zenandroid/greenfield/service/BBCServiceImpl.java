package io.zenandroid.greenfield.service;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.zenandroid.greenfield.api.BBCRadioApi;
import io.zenandroid.greenfield.model.PlaylistResponse;
import io.zenandroid.greenfield.util.EspressoIdlingResource;

public class BBCServiceImpl implements BBCService{

	private final static String TAG = BBCServiceImpl.class.getSimpleName();

	private final BBCRadioApi bbcRadioApi;

	@Inject
	public BBCServiceImpl(BBCRadioApi api) {
		bbcRadioApi = api;
	}

	@Override
	public Single<PlaylistResponse> fetchSongs() {
		EspressoIdlingResource.getInstance().increment();
		return bbcRadioApi.getPlaylistResponse()
				.subscribeOn(Schedulers.computation())
				.observeOn(AndroidSchedulers.mainThread())
				.doFinally(() -> EspressoIdlingResource.getInstance().decrement());
	}
}
