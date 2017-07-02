package io.zenandroid.greenfield.dagger;

import dagger.Component;
import io.zenandroid.greenfield.base.BaseActivity;
import io.zenandroid.greenfield.playlist.PlaylistPresenter;

/**
 * Created by acristescu on 22/06/2017.
 */

@Component(modules={AppModule.class, BBCServiceModule.class})
public interface AppComponent {

	void inject(BaseActivity activity);

	void inject(PlaylistPresenter presenter);


}
