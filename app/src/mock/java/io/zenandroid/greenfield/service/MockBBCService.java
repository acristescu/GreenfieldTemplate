package io.zenandroid.greenfield.service;

import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.zenandroid.greenfield.model.Playlist;
import io.zenandroid.greenfield.model.PlaylistResponse;
import io.zenandroid.greenfield.model.Song;
import io.zenandroid.greenfield.util.EspressoIdlingResource;

/**
 * Created by acristescu on 30/06/2017.
 */

public class MockBBCService implements BBCService {

	private PlaylistResponse mockResponse = new PlaylistResponse();
	private int delay = 0;

	@Inject public MockBBCService() {
		final Playlist playlist = new Playlist();
		mockResponse.setPlaylist(playlist);
		playlist.setSongs(new ArrayList<Song>());

		Song s;
		for(int i = 0 ; i < 5 ; i++) {
			s = new Song();
			s.setArtist("Artist " + i);
			s.setTitle("Title " + i);
			s.setImage("Image " + i);
			playlist.getSongs().add(s);
		}

	}

	@Override
	public Single<PlaylistResponse> fetchSongs() {
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
