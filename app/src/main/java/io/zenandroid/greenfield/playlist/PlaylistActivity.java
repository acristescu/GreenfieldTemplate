package io.zenandroid.greenfield.playlist;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.zenandroid.greenfield.R;
import io.zenandroid.greenfield.base.BaseActivity;
import io.zenandroid.greenfield.dagger.Injector;
import io.zenandroid.greenfield.model.Song;

public class PlaylistActivity extends BaseActivity implements PlaylistContract.View {

	@BindView(R.id.text) TextView textView;

	private PlaylistContract.Presenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Injector.get().inject(this);
		ButterKnife.bind(this);

		presenter = new PlaylistPresenter(this);
		presenter.start();
	}

	@Override
	public void displaySongs(List<Song> songs) {
		final StringBuilder sb = new StringBuilder();
		for(Song song : songs) {
			sb		.append(song.getArtist())
					.append(" - ")
					.append(song.getTitle());
			if(sb.length() != 0) {
				sb.append("\n");
			}
		}
		textView.setText(sb.toString());
	}
}
