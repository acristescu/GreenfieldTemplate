package io.zenandroid.greenfield.base;

/**
 * Created by acristescu on 02/07/2017.
 */

public interface View<T extends Presenter> {

	void showErrorMessage(String message);

	void showProgressDialog();

	void dismissProgressDialog();
}
