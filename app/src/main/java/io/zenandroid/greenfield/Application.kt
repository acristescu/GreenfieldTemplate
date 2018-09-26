package io.zenandroid.greenfield

import android.content.Context

import io.zenandroid.greenfield.dagger.Injector

class Application : android.app.Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Injector.init(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {

        var instance: Application? = null
            private set

        init {
            //
            // Note: if using vector images, you may want to do uncomment this line
            //
            //		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

}
