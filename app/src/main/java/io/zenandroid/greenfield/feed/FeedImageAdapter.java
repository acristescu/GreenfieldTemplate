package io.zenandroid.greenfield.feed;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.zenandroid.greenfield.R;
import io.zenandroid.greenfield.model.Image;

/**
 * Created by alex on 25/01/2018.
 */

public class FeedImageAdapter extends RecyclerView.Adapter<FeedImageAdapter.ViewHolder> {

    private List<Image> imageList = new ArrayList<>();
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.UK);
    private final PublishSubject<Image> browseSubject = PublishSubject.create();
    private final PublishSubject<Image> saveSubject = PublishSubject.create();
    private final PublishSubject<Image> shareSubject = PublishSubject.create();

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Image image = imageList.get(position);
        Picasso
                .with(holder.image.getContext())
                .load(image.getMedia().getM())
                .placeholder(R.drawable.progress_drawable)
                .into(holder.image);

        setTextOrEmpty(holder.title, image.getTitle(), R.string.no_title);
        setTextOrEmpty(holder.author, image.getAuthor(), R.string.no_author);
        setTextOrEmpty(holder.tags, image.getTags(), R.string.no_tags);

        holder.browseButton.setOnClickListener(v -> browseSubject.onNext(image));
        holder.saveButton.setOnClickListener(v -> saveSubject.onNext(image));
        holder.shareButton.setOnClickListener(v -> shareSubject.onNext(image));

        holder.publishedDate.setText(String.format("Published: %s", format.format(image.getPublishedDate())));
        holder.takenDate.setText(String.format("Taken: %s", format.format(image.getDateTaken())));
    }

    private void setTextOrEmpty(TextView target, String text, @StringRes int emptyString) {
        if(text == null || text.isEmpty()) {
            target.setText(emptyString);
        } else {
            target.setText(text);
        }
    }

    public Observable<Image> browseImageClicks() {
        return browseSubject.hide();
    }

    public Observable<Image> saveImageClicks() {
        return saveSubject.hide();
    }

    public Observable<Image> shareImageClicks() {
        return shareSubject.hide();
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image) ImageView image;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.author) TextView author;
        @BindView(R.id.tags) TextView tags;
        @BindView(R.id.published_date) TextView publishedDate;
        @BindView(R.id.taken_date) TextView takenDate;
        @BindView(R.id.browse) View browseButton;
        @BindView(R.id.save) View saveButton;
        @BindView(R.id.share) View shareButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
