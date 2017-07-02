package io.zenandroid.greenfield.model;

import java.util.List;

/**
 * Created by acristescu on 26/01/2017.
 */

public class Playlist {
	List<Song> a;
	List<Song> b;
	List<Song> c;

	public List<Song> getSongs() {
		return a;
	}

	public void setSongs(List<Song> songs) {
		a = songs;
	}
}
