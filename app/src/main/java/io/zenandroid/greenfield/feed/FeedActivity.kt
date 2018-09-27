package io.zenandroid.greenfield.feed

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.zenandroid.greenfield.R
import io.zenandroid.greenfield.base.BaseActivity
import io.zenandroid.greenfield.dagger.Injector
import io.zenandroid.greenfield.model.Image
import io.zenandroid.greenfield.service.FlickrService
import kotlinx.android.synthetic.main.activity_feed.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import javax.inject.Inject

/**
 * Created by alex on 25/01/2018.
 */
@RuntimePermissions
class FeedActivity : BaseActivity(), FeedContract.View {

    private lateinit var presenter: FeedContract.Presenter
    private val adapter = FeedImageAdapter()

    @Inject lateinit var flickrService: FlickrService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.get().inject(this)
        setContentView(R.layout.activity_feed)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.public_images)

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener { presenter.onRefresh() }
        adapter.browseImageClicks().subscribe { image -> presenter.onBrowseImage(image) }
        adapter.shareImageClicks().subscribe { image -> presenter.onShareImage(image) }
        adapter.saveImageClicks().subscribe { image -> presenter.onSaveImage(image) }

        presenter = FeedPresenter(this, flickrService)
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                presenter.onSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        sort.setOnClickListener { onSortClicked() }
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun showImages(items: List<Image>) {
        adapter.setImageList(items)
    }

    override fun closeSearchBox() {
        searchView.onActionViewCollapsed()
    }

    override fun setSubTitle(subtitle: String) {
        supportActionBar?.subtitle = subtitle
    }

    override fun browseURL(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }

    override fun loadImageFromURL(url: String, target: Target) {
        Picasso.with(this)
                .load(url)
                .into(target)
    }

    override fun saveBitmapToGallery(bitmap: Bitmap, title: String, description: String) {
        saveBitmapToGalleryImplWithPermissionCheck(bitmap, title, description)
    }

    override fun sendEmail(url: String) {
        val TO = arrayOf("")
        val CC = arrayOf("")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
        emailIntent.putExtra(Intent.EXTRA_TEXT, url)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
        emailIntent.putExtra(Intent.EXTRA_CC, CC)
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)))
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun saveBitmapToGalleryImpl(bitmap: Bitmap, title: String, description: String) {
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, title, description)
        if (path == null) {
            showErrorMessage("Save failed")
        } else {
            Toast.makeText(this, "Saved under $path", Toast.LENGTH_LONG).show()
        }
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun showPermissionDeniedMessage() {
        showErrorMessage("Cannot save image because permission request was rejected")
    }

    private fun onSortClicked() {
        val d = AlertDialog.Builder(this)
                .setTitle("Select sorting")
                .setItems(arrayOf("Date Published", "Date Taken")) { _, position ->
                    when (position) {
                        0 -> presenter.onSortBy(FeedContract.SortCriterion.PUBLISHED)
                        1 -> presenter.onSortBy(FeedContract.SortCriterion.TAKEN)
                    }
                }
                .create()
        d.show()
    }

    override fun showProgressDialog() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun dismissProgressDialog() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }
}
