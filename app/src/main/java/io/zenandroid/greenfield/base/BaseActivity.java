package io.zenandroid.greenfield.base;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by acristescu on 24/06/2017.
 */

public class BaseActivity extends AppCompatActivity {

	private ProgressDialog progressDialog;
	private View customLoadingView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		customLoadingView = new ProgressBar(this);
	}
	public void showErrorMessage(String message) {
		dismissProgressDialog();
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public void showProgressDialog() {
		ViewGroup parent = (ViewGroup) customLoadingView.getParent();

		if(progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);
			progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		}

		if (parent != null) {
			parent.removeView(customLoadingView);
		}

		progressDialog.show();
		progressDialog.setContentView(customLoadingView);
	}

	public void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgressDialog();
	}
}
