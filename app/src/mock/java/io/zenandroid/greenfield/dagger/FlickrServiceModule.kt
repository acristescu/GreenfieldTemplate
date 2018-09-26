package io.zenandroid.greenfield.dagger

import dagger.Binds
import dagger.Module
import io.zenandroid.greenfield.service.FlickrService
import io.zenandroid.greenfield.service.MockFlickrService

/**
 * created by acristescu
 */
@Module
abstract class FlickrServiceModule {

    @Binds
    internal abstract fun provideFlickrService(bbcService: MockFlickrService): FlickrService
}
