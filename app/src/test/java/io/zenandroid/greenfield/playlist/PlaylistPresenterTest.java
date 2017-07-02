package io.zenandroid.greenfield.playlist;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import io.zenandroid.greenfield.Application;
import io.zenandroid.greenfield.dagger.DaggerTestingComponent;
import io.zenandroid.greenfield.dagger.Injector;
import io.zenandroid.greenfield.dagger.MockBBCServiceModule;
import io.zenandroid.greenfield.event.ApiError;
import io.zenandroid.greenfield.model.Playlist;
import io.zenandroid.greenfield.model.PlaylistResponse;
import io.zenandroid.greenfield.model.Song;
import io.zenandroid.greenfield.service.BBCService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by acristescu on 02/07/2017.
 */

public class PlaylistPresenterTest {

	@Mock PlaylistContract.View view;

	@Mock BBCService service;

	private PlaylistPresenter presenter;
	private PlaylistResponse response;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Injector.setComponent(
				DaggerTestingComponent.builder().
						mockBBCServiceModule(new MockBBCServiceModule(service)).build());
		Application.setBus(new Bus(ThreadEnforcer.ANY));
		presenter = new PlaylistPresenter(view);

		response = new PlaylistResponse();
		final Playlist playlist = new Playlist();
		response.setPlaylist(playlist);
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

	@Test
	public void testSongsAreLoaded() {
		presenter.start();

		verify(view).showProgressDialog();
		verify(service).fetchSongs();

		Application.getBus().post(response);
		verify(view).displaySongs(response.getPlaylist().getSongs());
		verify(view).dismissProgressDialog();

		verifyNoMoreInteractions(view);
		verifyNoMoreInteractions(service);
	}

	@Test
	public void testApiErrorIsHandled() {
		presenter.start();

		verify(view).showProgressDialog();
		verify(service).fetchSongs();

		final ApiError error = new ApiError("Test message", new Exception(), PlaylistResponse.class);
		Application.getBus().post(error);
		verify(view).dismissProgressDialog();
		verify(view).showErrorMessage(error.getMessage());

		verifyNoMoreInteractions(view);
		verifyNoMoreInteractions(service);
	}
}
