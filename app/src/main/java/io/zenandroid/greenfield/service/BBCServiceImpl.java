package io.zenandroid.greenfield.service;

import javax.inject.Inject;

import io.zenandroid.greenfield.api.ApiCallback;
import io.zenandroid.greenfield.api.BBCRadioApi;
import io.zenandroid.greenfield.model.PlaylistResponse;

public class BBCServiceImpl implements BBCService{

	private final static String TAG = BBCServiceImpl.class.getSimpleName();

	private final BBCRadioApi bbcRadioApi;

	@Inject
	public BBCServiceImpl(BBCRadioApi api) {
		bbcRadioApi = api;
	}

	@Override
	public void fetchSongs() {
		bbcRadioApi.getPlaylistResponse().enqueue(new ApiCallback<>(PlaylistResponse.class));
	}
}
