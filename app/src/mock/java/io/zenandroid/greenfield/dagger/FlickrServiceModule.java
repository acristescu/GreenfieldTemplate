package io.zenandroid.greenfield.dagger;

import dagger.Binds;
import dagger.Module;
import io.zenandroid.greenfield.service.FlickrService;
import io.zenandroid.greenfield.service.MockFlickrService;

/**
 * created by acristescu
 */
@Module
public abstract class FlickrServiceModule {

	@Binds
	abstract FlickrService provideBBCService(MockFlickrService bbcService);
}
