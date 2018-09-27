package io.zenandroid.greenfield.feed

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.zenandroid.greenfield.R
import io.zenandroid.greenfield.model.Image
import kotlinx.android.synthetic.main.item_feed_image.view.*
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
                .with(holder.itemView.image.context)
                .load(image.media?.m)
                .placeholder(R.drawable.progress_drawable)
                .into(holder.itemView.image)

        setTextOrEmpty(holder.itemView.title, image.title, R.string.no_title)
        setTextOrEmpty(holder.itemView.author, image.author, R.string.no_author)
        setTextOrEmpty(holder.itemView.tags, image.tags, R.string.no_tags)

        holder.itemView.browse.setOnClickListener { _ -> browseSubject.onNext(image) }
        holder.itemView.save.setOnClickListener { _ -> saveSubject.onNext(image) }
        holder.itemView.share.setOnClickListener { _ -> shareSubject.onNext(image) }

        holder.itemView.publishedDate.text = String.format("Published: %s", format.format(image.published))
        holder.itemView.takenDate.text = String.format("Taken: %s", format.format(image.dateTaken))
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
