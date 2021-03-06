package io.zenandroid.greenfield.dagger

import dagger.Component
import io.zenandroid.greenfield.feed.FeedActivity

/**
 * Created by acristescu on 22/06/2017.
 */

@Component(modules = [AppModule::class, FlickrServiceModule::class])
interface AppComponent {

    fun inject(activity: FeedActivity)


}
