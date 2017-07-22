package io.zenandroid.greenfield.playlist;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import io.reactivex.Single;
import io.zenandroid.greenfield.dagger.DaggerTestingComponent;
import io.zenandroid.greenfield.dagger.Injector;
import io.zenandroid.greenfield.dagger.MockBBCServiceModule;
import io.zenandroid.greenfield.model.Playlist;
import io.zenandroid.greenfield.model.PlaylistResponse;
import io.zenandroid.greenfield.model.Song;
import io.zenandroid.greenfield.service.BBCService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
						mockBBCServiceModule(new MockBBCServiceModule(service)).build()
		);
		presenter = new PlaylistPresenter(view);

		response = new PlaylistResponse();
		final Playlist playlist = new Playlist();
		response.setPlaylist(playlist);
		playlist.setSongs(new ArrayList<>());

		Song s;
		for(int i = 0 ; i < 5 ; i++) {
			s = new Song();
			s.setArtist("Artist " + i);
			s.setTitle("Title " + i);
			s.setImage("Image " + i);
			playlist.getSongs().add(s);
		}

		when(service.fetchSongs()).thenReturn(Single.just(response));
	}

	@Test
	public void testSongsAreLoaded() {
		presenter.subscribe();

		verify(view).showProgressDialog();
		verify(service).fetchSongs();

		verify(view).displaySongs(response.getPlaylist().getSongs());
		verify(view).dismissProgressDialog();

		verifyNoMoreInteractions(view);
		verifyNoMoreInteractions(service);
		presenter.unsubscribe();
	}

	@Test
	public void testApiErrorIsHandled() {
		final Exception exception = new Exception("An exception");
		when(service.fetchSongs()).thenReturn(Single.error(exception));

		presenter.subscribe();

		verify(view).showProgressDialog();
		verify(service).fetchSongs();

		verify(view).dismissProgressDialog();
		verify(view).showErrorMessage(exception.getMessage());

		verifyNoMoreInteractions(view);
		verifyNoMoreInteractions(service);
		presenter.unsubscribe();
	}
}
