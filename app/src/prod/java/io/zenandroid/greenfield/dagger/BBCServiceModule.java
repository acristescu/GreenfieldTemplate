package io.zenandroid.greenfield.dagger;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import io.zenandroid.greenfield.BuildConfig;
import io.zenandroid.greenfield.api.BBCRadioApi;
import io.zenandroid.greenfield.service.BBCService;
import io.zenandroid.greenfield.service.BBCServiceImpl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by acristescu on 30/06/2017.
 */
@Module
public class BBCServiceModule {

	private static final int TIMEOUT = 15;

	@Provides
	BBCRadioApi provideBBCRadioApi() {
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

		return new Retrofit
				.Builder()
				.baseUrl(BuildConfig.BASE_URL)
				.client(okClientBuilder.build())
				.addConverterFactory(GsonConverterFactory.create())
				.build()
				.create(BBCRadioApi.class);
	}

	@Provides
	BBCService provideBBCService(BBCServiceImpl bbcService) {
		return bbcService;
	}

}
