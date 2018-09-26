package io.zenandroid.greenfield.dagger

/**
 * Created by acristescu on 22/06/2017.
 */

import android.content.Context
import android.support.annotation.VisibleForTesting

object Injector {

    private var component: AppComponent? = null

    fun init(context: Context) {
        if (component == null) {
            component = DaggerAppComponent.builder().appModule(AppModule()).build()
        }
    }

    fun get(): AppComponent {
        return component!!
    }

    @VisibleForTesting
    fun setComponent(component: AppComponent) {
        Injector.component = component
    }
}
