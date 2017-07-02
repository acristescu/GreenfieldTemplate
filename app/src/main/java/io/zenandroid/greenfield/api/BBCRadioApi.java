package io.zenandroid.greenfield.api;

import io.zenandroid.greenfield.model.PlaylistResponse;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by acristescu on 26/01/2017.
 */

public interface BBCRadioApi {

	@GET("playlist.json")
	Call<PlaylistResponse> getPlaylistResponse();
}
