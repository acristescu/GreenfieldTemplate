package io.zenandroid.greenfield.dagger;

import dagger.Binds;
import dagger.Module;
import io.zenandroid.greenfield.service.BBCService;
import io.zenandroid.greenfield.service.MockBBCService;

/**
 * Created by acristescu on 30/06/2017.
 */
@Module
public abstract class BBCServiceModule {

	@Binds
	abstract BBCService provideBBCService(MockBBCService bbcService);
}
