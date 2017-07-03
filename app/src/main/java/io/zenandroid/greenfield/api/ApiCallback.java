package io.zenandroid.greenfield.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.otto.Bus;

import io.zenandroid.greenfield.Application;
import io.zenandroid.greenfield.R;
import io.zenandroid.greenfield.event.ApiError;
import io.zenandroid.greenfield.event.EmptyBodyResponse;
import io.zenandroid.greenfield.util.EspressoIdlingResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Generic API callback that allows consistent error handling. If the caller needs to process
 * the result before it's posted on the bus (e.g. cache it?), he/she can override the processResult
 * method for the purpose.
 */
public class ApiCallback<T> implements Callback<T> {

	private Class<? extends T> responseClass;

	private final Bus bus = Application.getBus();

	private static final String TAG = ApiCallback.class.getSimpleName();

	public ApiCallback(Class<? extends T> responseClass) {
		EspressoIdlingResource.getInstance().increment();
		this.responseClass = responseClass;
	}

	@Override
	public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
		EspressoIdlingResource.getInstance().decrement();
		if(response.isSuccessful()) {
			if(response.body() != null) {
				bus.post(processResult(response.body()));
			} else {
				bus.post(generateEmptyBodyResult());
			}
		} else {
			final String message = String.format(
					Application.getInstance().getString(R.string.api_error_message),
					response.code(),
					response.message()
			);

			Log.e(TAG, message);
			bus.post(new ApiError(message, null, responseClass));
		}
	}

	@Override
	public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
		EspressoIdlingResource.getInstance().decrement();
		Log.e(TAG, t.getMessage(), t);
		bus.post(new ApiError(Application.getInstance().getString(R.string.error_server_connection), t, responseClass));
	}

	/**
	 * Override this to inspect (and possibly modify) the raw result before it's posted on the bus.
	 * You could for example cache the result or apply some business logic to it.
	 * @param rawResult the result as it came from the backend
	 * @return the result that you want posted on the bus
	 */
	protected T processResult(T rawResult) {
		return rawResult;
	}

	protected Object generateEmptyBodyResult() {
		return new EmptyBodyResponse();
	}
}
