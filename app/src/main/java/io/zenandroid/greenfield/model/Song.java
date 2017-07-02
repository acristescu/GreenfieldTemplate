package io.zenandroid.greenfield.model;

import java.util.Objects;

/**
 * Created by acristescu on 26/01/2017.
 */

public class Song {
	private String title;
	private String artist;
	private String image;

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getImage() {
		return image;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Song song = (Song) o;
		return Objects.equals(title, song.title) &&
				Objects.equals(artist, song.artist) &&
				Objects.equals(image, song.image);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, artist, image);
	}
}
