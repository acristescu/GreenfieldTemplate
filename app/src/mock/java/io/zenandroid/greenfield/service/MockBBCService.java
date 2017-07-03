package io.zenandroid.greenfield.service;

import android.os.Handler;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;

import javax.inject.Inject;

import io.zenandroid.greenfield.Application;
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
	public void fetchSongs() {
		EspressoIdlingResource.getInstance().increment();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Application.getBus().post(mockResponse);
				EspressoIdlingResource.getInstance().decrement();
			}
		}, delay);
	}

	@VisibleForTesting
	public void setDelay(int delay) {
		this.delay = delay;
	}
}
