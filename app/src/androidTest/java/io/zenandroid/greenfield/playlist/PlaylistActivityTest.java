package io.zenandroid.greenfield.playlist;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.zenandroid.greenfield.R;
import io.zenandroid.greenfield.util.EspressoIdlingResource;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

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
		for(int i = 0 ; i < 5 ; i++ ) {
			onView(subfieldOfNthItemWithId(withId(R.id.recycler), i, withId(R.id.title)))
					.check(matches(withText("Title " + i)));
			onView(subfieldOfNthItemWithId(withId(R.id.recycler), i, withId(R.id.artist)))
					.check(matches(withText("Artist " + i)));
		}
	}

	public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("with "+childPosition+" child view of type parentMatcher");
			}

			@Override
			public boolean matchesSafely(View view) {
				if (!(view.getParent() instanceof ViewGroup)) {
					return parentMatcher.matches(view.getParent());
				}

				ViewGroup group = (ViewGroup) view.getParent();
				return parentMatcher.matches(view.getParent()) && group.getChildAt(childPosition).equals(view);
			}
		};
	}

	/**
	 * Matches a view that is a descendant of the nth item in a recyclerview
	 * @param listMatcher
	 * @param childPosition
	 * @param subviewMatcher
	 * @return
	 */
	public static Matcher<View> subfieldOfNthItemWithId(final Matcher<View> listMatcher, final int childPosition, final Matcher<View> subviewMatcher) {
		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Sub-view of an item from a list");
			}

			@Override
			public boolean matchesSafely(View view) {
				//
				// Clearly "espresso + recyclerview != love"
				//
				return allOf(
						isDescendantOfA(nthChildOf(listMatcher, childPosition)),
						subviewMatcher
				).matches(view);
			}
		};
	}
}
