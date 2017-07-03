package io.zenandroid.greenfield.playlist;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.zenandroid.greenfield.R;
import io.zenandroid.greenfield.util.EspressoIdlingResource;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by acristescu on 03/07/2017.
 */

@RunWith(AndroidJUnit4.class)
public class PlaylistActivityTest {

	@Rule
	public ActivityTestRule<PlaylistActivity> playlistActivityTestRule = new ActivityTestRule<>(PlaylistActivity.class);

	@Before
	public void registerIdlingResource() {
		Espresso.registerIdlingResources(EspressoIdlingResource.getInstance());
	}

	@After
	public void unregisterIdlingResource() {
		Espresso.unregisterIdlingResources(EspressoIdlingResource.getInstance());
	}

	@Test
	public void testActivityLoads() {
		onView(withId(R.id.text)).check(matches(isDisplayed()));
		onView(withId(R.id.text)).check(matches(withText("Artist 0 - Title 0\nArtist 1 - Title 1\nArtist 2 - Title 2\nArtist 3 - Title 3\nArtist 4 - Title 4\n")));
	}
}
