package io.zenandroid.greenfield.dagger

/**
 * Created by acristescu on 22/06/2017.
 */

import android.content.Context
import androidx.annotation.VisibleForTesting

object Injector {

    private lateinit var component: AppComponent

    fun init(context: Context) {
        component = DaggerAppComponent.builder().appModule(AppModule()).build()
    }

    fun get(): AppComponent {
        return component
    }

    @VisibleForTesting
    fun setComponent(component: AppComponent) {
        Injector.component = component
    }
}
