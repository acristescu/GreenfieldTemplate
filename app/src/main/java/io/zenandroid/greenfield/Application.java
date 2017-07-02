package io.zenandroid.greenfield;

import android.content.Context;

import io.zenandroid.greenfield.dagger.Injector;

public class Application extends android.app.Application {

	private static Application instance;

	static {
		//
		// Note: if using vector images, you may want to do uncomment this line
		//
//		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		Injector.init(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public static Application getInstance() {
		return instance;
	}
}
