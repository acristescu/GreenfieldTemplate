package io.zenandroid.greenfield.playlist;

import java.util.List;

import io.zenandroid.greenfield.model.Song;

/**
 * Created by acristescu on 02/07/2017.
 */

public interface PlaylistContract {
	interface View extends io.zenandroid.greenfield.base.View<Presenter> {
		void displaySongs(List<Song> songs);
	}

	interface Presenter extends io.zenandroid.greenfield.base.Presenter {

	}
}
