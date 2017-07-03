package io.zenandroid.greenfield.playlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.zenandroid.greenfield.R;
import io.zenandroid.greenfield.model.Song;

/**
 * Created by acristescu on 26/01/2017.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

	private List<Song> mSongs;

	private SongListAdapter(){}

	public SongListAdapter(List<Song> songs) {
		mSongs = songs;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_song, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final Song song = mSongs.get(position);
		holder.title.setText(song.getTitle());
		holder.artist.setText(song.getArtist());
		Picasso
				.with(holder.image.getContext())
				.load(song.getImage())
				.placeholder(R.drawable.ic_music_video_black_24dp)
				.into(holder.image);
	}

	@Override
	public int getItemCount() {
		return mSongs.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.title) TextView title;
		@BindView(R.id.artist) TextView artist;
		@BindView(R.id.image) ImageView image;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
