package io.zenandroid.greenfield.service;

import io.reactivex.Single;
import io.zenandroid.greenfield.model.PlaylistResponse;

/**
 * Created by acristescu on 30/06/2017.
 */

public interface BBCService {
	Single<PlaylistResponse> fetchSongs();
}
