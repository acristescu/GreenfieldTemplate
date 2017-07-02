package io.zenandroid.greenfield.dagger;

import dagger.Component;

/**
 * Created by acristescu on 02/07/2017.
 */
@Component(modules={AppModule.class, MockBBCServiceModule.class})
public interface TestingComponent extends AppComponent {
}
