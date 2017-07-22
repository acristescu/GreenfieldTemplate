package io.zenandroid.greenfield.api;

import io.reactivex.Single;
import io.zenandroid.greenfield.model.PlaylistResponse;
import retrofit2.http.GET;

/**
 * Created by acristescu on 26/01/2017.
 */

public interface BBCRadioApi {

	@GET("plasylist.json")
	Single<PlaylistResponse> getPlaylistResponse();
}
