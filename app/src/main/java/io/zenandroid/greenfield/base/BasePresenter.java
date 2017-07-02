package io.zenandroid.greenfield.base;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.zenandroid.greenfield.event.ApiError;

/**
 * Created by acristescu on 02/07/2017.
 */

public abstract class BasePresenter implements Presenter {

	private View view;

	public BasePresenter(View view) {
		this.view = view;
		EventBus.getDefault().register(this);
	}

	@Subscribe
	protected void onApiError(ApiError error) {
		view.dismissProgressDialog();
		view.showErrorMessage(error.getMessage());
	}
}
