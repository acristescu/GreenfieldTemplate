package io.zenandroid.greenfield.base

/**
 * Created by acristescu on 02/07/2017.
 */

interface View<T : Presenter> {

    fun showErrorMessage(message: String?)

    fun showProgressDialog()

    fun dismissProgressDialog()
}
