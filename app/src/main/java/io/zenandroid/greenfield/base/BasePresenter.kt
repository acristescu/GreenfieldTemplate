package io.zenandroid.greenfield.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by acristescu on 02/07/2017.
 */

abstract class BasePresenter(private val view: View<*>) : Presenter {
    private val compositeDisposable = CompositeDisposable()

    protected fun onError(throwable: Throwable) {
        view.dismissProgressDialog()
        view.showErrorMessage(throwable.message)
    }

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun unsubscribe() {
        compositeDisposable.clear()
    }
}
