package io.zenandroid.greenfield.base

import android.app.ProgressDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.LayoutRes

/**
 * Created by acristescu on 24/06/2017.
 */

open class BaseActivity : AppCompatActivity {

    constructor() : super()
    constructor(@LayoutRes contentLayoutId : Int)  : super(contentLayoutId)

    private var progressDialog: ProgressDialog? = null
    private lateinit var customLoadingView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customLoadingView = ProgressBar(this)
    }

    open fun showErrorMessage(message: String?) {
        dismissProgressDialog()
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    open fun showProgressDialog() {
        val parent = customLoadingView.parent as? ViewGroup

        if (progressDialog == null) {
            progressDialog = ProgressDialog(this).apply {
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            }
        }

        parent?.removeView(customLoadingView)

        progressDialog?.apply {
            show()
            setContentView(customLoadingView)
        }
    }

    open fun dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissProgressDialog()
    }
}
