package io.zenandroid.greenfield.dagger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import io.zenandroid.greenfield.BuildConfig;
import io.zenandroid.greenfield.api.FlickrApi;
import io.zenandroid.greenfield.service.FlickrService;
import io.zenandroid.greenfield.service.FlickrServiceImpl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * created by acristescu
 */
@Module
public class FlickrServiceModule {

	private static final int TIMEOUT = 15;

	@Provides
	FlickrApi provideFlickrApi() {
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);

		OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder()
//				.addInterceptor(new Interceptor() {
//					@Override
//					public Response intercept(Chain chain) throws IOException {
//						Request request = chain.request();
//						if(credentialsManager.getToken() != null) {
//							request = request
//									.newBuilder()
//									.addHeader("Authorization", "Bearer " + credentialsManager.getToken())
//									.build();
//						}
//						return chain.proceed(request);
//					}
//				})
				.connectTimeout(TIMEOUT, TimeUnit.SECONDS)
				.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(TIMEOUT, TimeUnit.SECONDS)
				.addInterceptor(logging);

		final Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.create();

		return new Retrofit
				.Builder()
				.baseUrl(BuildConfig.BASE_URL)
				.client(okClientBuilder.build())
				.addConverterFactory(GsonConverterFactory.create(gson))
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
				.create(FlickrApi.class);
	}

	@Provides
	FlickrService provideFlickrService(FlickrServiceImpl service) {
		return service;
	}

}
