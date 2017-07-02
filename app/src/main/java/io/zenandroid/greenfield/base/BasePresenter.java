package io.zenandroid.greenfield.base;

import com.squareup.otto.Subscribe;

import io.zenandroid.greenfield.Application;
import io.zenandroid.greenfield.event.ApiError;

/**
 * Created by acristescu on 02/07/2017.
 */

public abstract class BasePresenter implements Presenter {

	private View view;

	public BasePresenter(View view) {
		this.view = view;
		//
		// This hack is needed because otto does not walk up the object hierarchy when posting a
		// message (due to performance reasons) hence you cannot subscribe to a message in a base
		// class. To work around this bug/feature we specifically register on the error message here
		// This is the recommended way of doing it. See https://github.com/square/otto/issues/26
		//
		Application.getBus().register(new Object() {
			@Subscribe
			public void onApiError(ApiError error) {
				BasePresenter.this.onApiError(error);
			}
		});
		Application.getBus().register(this);
	}

	@Subscribe
	protected void onApiError(ApiError error) {
		view.dismissProgressDialog();
		view.showErrorMessage(error.getMessage());
	}
}
