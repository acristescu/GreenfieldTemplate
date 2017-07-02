package io.zenandroid.greenfield;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.squareup.otto.Bus;

import io.zenandroid.greenfield.dagger.Injector;

public class Application extends android.app.Application {

	private static Application instance;

	private static Bus bus = new Bus();

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

	public static Bus getBus() {
		return bus;
	}

	@VisibleForTesting
	public static void setBus(Bus bus) {
		Application.bus = bus;
	}

}
