package io.zenandroid.greenfield.feed;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.zenandroid.greenfield.R;
import io.zenandroid.greenfield.model.Image;
import io.zenandroid.greenfield.model.ImageListResponse;
import io.zenandroid.greenfield.util.EspressoIdlingResource;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.internal.deps.guava.base.Strings.isNullOrEmpty;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNot.not;

/**
 * created by acristescu
 */

@RunWith(AndroidJUnit4.class)
public class FeedActivityTest {

	@Rule
	public ActivityTestRule<FeedActivity> feedActivityActivityTestRule = new ActivityTestRule<>(FeedActivity.class);
	private ImageListResponse mockResponse;

	@Before
	public void registerIdlingResource() {
		Espresso.registerIdlingResources(EspressoIdlingResource.getInstance());
		final Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.create();
		mockResponse = gson.fromJson(
				new InputStreamReader(getClass().getClassLoader().getResourceAsStream("mock_data.json")),
				ImageListResponse.class
		);
	}

	@After
	public void unregisterIdlingResource() {
		Espresso.unregisterIdlingResources(EspressoIdlingResource.getInstance());
	}

	@Test
	public void testActivityLoads() {
		final List<Image> expected = getListSortedByPublishedDate(mockResponse.getItems());
		checkItems(expected);
	}

	@Test
	public void testSortWorks() {
		final List<Image> expected = getListSortedByTakenDate(mockResponse.getItems());
		onView(withId(R.id.sort)).perform(click());
		onView(withText("Date Taken")).perform(click());
		checkItems(expected);
	}

	@Test
	public void testBrowseWorks() {
		final List<Image> expected = getListSortedByPublishedDate(mockResponse.getItems());
		final Matcher<Intent> intentMatcher = allOf(
				hasAction(equalTo(Intent.ACTION_VIEW)),
				hasData(expected.get(0).getLink())
		);

		Intents.init();
		intending(intentMatcher).respondWith(new Instrumentation.ActivityResult(0, null));

		onView(first(withId(R.id.browse))).perform(click());

		intended(intentMatcher);
		Intents.release();
	}

	@Test
	public void testShareWorks() {
		final List<Image> expected = getListSortedByPublishedDate(mockResponse.getItems());
		final Matcher<Intent> intentMatcher = chooser(allOf(
				hasAction(equalTo(Intent.ACTION_SEND)),
				hasType("text/plain"),
				hasExtra(Intent.EXTRA_TEXT, expected.get(0).getMedia().getM())
		));
		Intents.init();
		intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

		onView(first(withId(R.id.share))).perform(click());

		intended(intentMatcher);
		Intents.release();
	}

	private void checkItems(List<Image> expected) {
		for(int i = 0 ; i < expected.size() ; i ++) {
			final Image img = expected.get(i);

			onView(withId(R.id.recycler)).perform(RecyclerViewActions.scrollToPosition(i));
			onView(withId(R.id.recycler)).check(matches(
					atPosition(i, hasDescendant(allOf(withId(R.id.title), withText(img.getTitle()))))
			));
			onView(withId(R.id.recycler)).check(matches(
					atPosition(i, hasDescendant(allOf(withId(R.id.tags), withText(isNullOrEmpty(img.getTags()) ? "(no tags)" : img.getTags()))))
			));
		}
	}

	private List<Image> getListSortedByPublishedDate(List<Image> original) {
		final List<Image> expected = new ArrayList<>(original);
		Collections.sort(expected, (o1, o2) -> {
			if(o1.getPublishedDate() == null) {
				return -1;
			}
			return o1.getPublishedDate().compareTo(o2.getPublishedDate());
		});

		return expected;
	}

	private List<Image> getListSortedByTakenDate(List<Image> original) {
		final List<Image> expected = new ArrayList<>(original);
		Collections.sort(expected, (o1, o2) -> {
			if(o1.getDateTaken() == null) {
				return -1;
			}
			return o1.getDateTaken().compareTo(o2.getDateTaken());
		});

		return expected;
	}

	public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
		return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
			@Override
			public void describeTo(Description description) {
				description.appendText("has item at position " + position + ": ");
				itemMatcher.describeTo(description);
			}

			@Override
			protected boolean matchesSafely(final RecyclerView view) {
				RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
				if (viewHolder == null) {
					// has no item on such position
					return false;
				}
				return itemMatcher.matches(viewHolder.itemView);
			}
		};
	}

	private <T> Matcher<T> first(final Matcher<T> matcher) {
		return new BaseMatcher<T>() {
			boolean isFirst = true;

			@Override
			public boolean matches(final Object item) {
				if (isFirst && matcher.matches(item)) {
					isFirst = false;
					return true;
				}

				return false;
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("should return first matching item");
			}
		};
	}

	public static Matcher<Intent> chooser(Matcher<Intent> matcher) {
		return allOf(hasAction(Intent.ACTION_CHOOSER), hasExtra(is(Intent.EXTRA_INTENT), matcher));
	}
}
