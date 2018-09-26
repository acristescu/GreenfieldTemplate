package io.zenandroid.greenfield.feed

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.zenandroid.greenfield.R
import io.zenandroid.greenfield.model.Image
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by alex on 25/01/2018.
 */

class FeedImageAdapter : RecyclerView.Adapter<FeedImageAdapter.ViewHolder>() {

    private var imageList: List<Image> = ArrayList()
    private val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.UK)
    private val browseSubject = PublishSubject.create<Image>()
    private val saveSubject = PublishSubject.create<Image>()
    private val shareSubject = PublishSubject.create<Image>()

    fun setImageList(imageList: List<Image>) {
        this.imageList = imageList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feed_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imageList[position]
        Picasso
                .with(holder.image.context)
                .load(image.media?.m)
                .placeholder(R.drawable.progress_drawable)
                .into(holder.image)

        setTextOrEmpty(holder.title, image.title, R.string.no_title)
        setTextOrEmpty(holder.author, image.author, R.string.no_author)
        setTextOrEmpty(holder.tags, image.tags, R.string.no_tags)

        holder.browseButton.setOnClickListener { v -> browseSubject.onNext(image) }
        holder.saveButton.setOnClickListener { v -> saveSubject.onNext(image) }
        holder.shareButton.setOnClickListener { v -> shareSubject.onNext(image) }

        holder.publishedDate.text = String.format("Published: %s", format.format(image.published))
        holder.takenDate.text = String.format("Taken: %s", format.format(image.dateTaken))
    }

    private fun setTextOrEmpty(target: TextView?, text: String?, @StringRes emptyString: Int) {
        if (text == null || text.isEmpty()) {
            target?.setText(emptyString)
        } else {
            target?.text = text
        }
    }

    fun browseImageClicks(): Observable<Image> {
        return browseSubject.hide()
    }

    fun saveImageClicks(): Observable<Image> {
        return saveSubject.hide()
    }

    fun shareImageClicks(): Observable<Image> {
        return shareSubject.hide()
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.image) lateinit var image: ImageView
        @BindView(R.id.title) lateinit var title: TextView
        @BindView(R.id.author) lateinit var author: TextView
        @BindView(R.id.tags) lateinit var tags: TextView
        @BindView(R.id.published_date) lateinit var publishedDate: TextView
        @BindView(R.id.taken_date) lateinit var takenDate: TextView
        @BindView(R.id.browse) lateinit var browseButton: View
        @BindView(R.id.save) lateinit var saveButton: View
        @BindView(R.id.share) lateinit var shareButton: View

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}
