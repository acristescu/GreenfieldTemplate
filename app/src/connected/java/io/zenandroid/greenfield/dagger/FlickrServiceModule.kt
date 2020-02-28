package io.zenandroid.greenfield.dagger

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.zenandroid.greenfield.BuildConfig
import io.zenandroid.greenfield.api.FlickrApi
import io.zenandroid.greenfield.service.FlickrService
import io.zenandroid.greenfield.service.FlickrServiceImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * created by acristescu
 */
@Module
class FlickrServiceModule {

    @Provides
    internal fun provideFlickrApi(): FlickrApi {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okClientBuilder = OkHttpClient.Builder()
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
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .addInterceptor(logging)

        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(FlickrApi::class.java)
    }

    @Provides
    internal fun provideFlickrService(service: FlickrServiceImpl): FlickrService {
        return service
    }

    companion object {

        private val TIMEOUT = 15
    }

}
