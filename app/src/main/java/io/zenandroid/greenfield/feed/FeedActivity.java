package io.zenandroid.greenfield.feed;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.zenandroid.greenfield.R;
import io.zenandroid.greenfield.base.BaseActivity;
import io.zenandroid.greenfield.dagger.Injector;
import io.zenandroid.greenfield.model.Image;
import io.zenandroid.greenfield.service.FlickrService;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by alex on 25/01/2018.
 */
@RuntimePermissions
public class FeedActivity extends BaseActivity implements FeedContract.View {

    private FeedContract.Presenter presenter;
    private FeedImageAdapter adapter = new FeedImageAdapter();

    @BindView(R.id.recycler) RecyclerView recycler;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.search_view) SearchView searchView;
    @BindView(R.id.swipe_to_refresh) SwipeRefreshLayout swipeRefreshLayout;

    @Inject FlickrService flickrService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.get().inject(this);
        setContentView(R.layout.activity_feed);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.public_images);

        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> presenter.onRefresh());
        adapter.browseImageClicks().subscribe(image -> presenter.onBrowseImage(image));
        adapter.shareImageClicks().subscribe(image -> presenter.onShareImage(image));
        adapter.saveImageClicks().subscribe(image -> presenter.onSaveImage(image));

        presenter = new FeedPresenter(this, flickrService);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void showImages(List<Image> items) {
        adapter.setImageList(items);
    }

    @Override
    public void closeSearchBox() {
        searchView.onActionViewCollapsed();
    }

    @Override
    public void setSubTitle(String subtitle) {
        getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void browseURL(String link) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(browserIntent);
    }

    @Override
    public void loadImageFromURL(String url, Target target) {
        Picasso.with(this)
                .load(url)
                .into(target);
    }

    @Override
    public void saveBitmapToGallery(Bitmap bitmap, String title, String description) {
        FeedActivityPermissionsDispatcher.saveBitmapToGalleryImplWithPermissionCheck(this, bitmap, title, description);
    }

    @Override
    public void sendEmail(String url) {
        final String[] TO = {""};
        final String[] CC = {""};
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT   , url);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void saveBitmapToGalleryImpl(Bitmap bitmap, String title, String description) {
        final String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, title , description);
        if(path == null) {
            showErrorMessage("Save failed");
        } else {
            Toast.makeText(this, "Saved under " + path, Toast.LENGTH_LONG).show();
        }
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showPermissionDeniedMessage() {
        showErrorMessage("Cannot save image because permission request was rejected");
    }

    @OnClick(R.id.sort)
    public void onSortClicked() {
        final Dialog d = new AlertDialog.Builder(this)
                .setTitle("Select sorting")
                .setItems(new String[]{"Date Published", "Date Taken"}, (dlg, position) -> {
                    switch (position) {
                        case 0:
                            presenter.onSortBy(FeedContract.SortCriterion.PUBLISHED);
                            break;
                        case 1:
                            presenter.onSortBy(FeedContract.SortCriterion.TAKEN);
                            break;
                    }
                })
                .create();
        d.show();
    }

    @Override
    public void showProgressDialog() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void dismissProgressDialog() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        FeedActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
