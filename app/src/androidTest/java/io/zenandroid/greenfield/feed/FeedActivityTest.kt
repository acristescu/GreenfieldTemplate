package io.zenandroid.greenfield.feed

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import androidx.test.espresso.IdlingRegistry
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.zenandroid.greenfield.R
import io.zenandroid.greenfield.model.Image
import io.zenandroid.greenfield.model.ImageListResponse
import io.zenandroid.greenfield.util.EspressoIdlingResource
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStreamReader

/**
 * created by acristescu
 */

@RunWith(AndroidJUnit4::class)
class FeedActivityTest {

    @Rule @JvmField
    var feedActivityActivityTestRule = ActivityTestRule(FeedActivity::class.java)
    private lateinit var mockResponse: ImageListResponse

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource)
        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        mockResponse = gson.fromJson(
                InputStreamReader(javaClass.classLoader!!.getResourceAsStream("assets/mock_data.json")),
                ImageListResponse::class.java
        )
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource)
    }

    @Test
    fun testActivityLoads() {
        val expected = getListSortedByPublishedDate(mockResponse.items!!)
        checkItems(expected)
    }

    @Test
    fun testSortWorks() {
        val expected = getListSortedByTakenDate(mockResponse.items!!)
        onView(withId(R.id.sort)).perform(click())
        onView(withText("Date Taken")).perform(click())
        checkItems(expected)
    }

    @Test
    fun testBrowseWorks() {
        val expected = getListSortedByPublishedDate(mockResponse.items!!)
        val intentMatcher = allOf(
                hasAction(equalTo(Intent.ACTION_VIEW)),
                hasData(expected[0].link)
        )

        Intents.init()
        intending(intentMatcher).respondWith(Instrumentation.ActivityResult(0, null))

        onView(first(withId(R.id.browse))).perform(click())

        intended(intentMatcher)
        Intents.release()
    }

    @Test
    fun testShareWorks() {
        val expected = getListSortedByPublishedDate(mockResponse.items!!)
        val intentMatcher = chooser(allOf(
                hasAction(equalTo(Intent.ACTION_SEND)),
                hasType("text/plain"),
                hasExtra<String>(Intent.EXTRA_TEXT, expected[0].media!!.m)
        ))
        Intents.init()
        intending(not(isInternal())).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        onView(first(withId(R.id.share))).perform(click())

        intended(intentMatcher)
        Intents.release()
    }

    private fun checkItems(expected: List<Image>) {
        for (i in expected.indices) {
            val (title, _, _, _, _, _, _, _, tags) = expected[i]

            onView(withId(R.id.recycler)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(i))
            onView(withId(R.id.recycler)).check(matches(
                    atPosition(i, hasDescendant(allOf(withId(R.id.title), withText(title))))
            ))
            onView(withId(R.id.recycler)).check(matches(
                    atPosition(i, hasDescendant(allOf(withId(R.id.tags), withText(if (tags.isNullOrEmpty()) "(no tags)" else tags))))
            ))
        }
    }

    private fun getListSortedByPublishedDate(original: List<Image>) =
            original.sortedBy { it.published }

    private fun getListSortedByTakenDate(original: List<Image>) =
            original.sortedBy { it.dateTaken }

    private fun <T> first(matcher: Matcher<T>): Matcher<T> {
        return object : BaseMatcher<T>() {
            var isFirst = true

            override fun matches(item: Any): Boolean {
                if (isFirst && matcher.matches(item)) {
                    isFirst = false
                    return true
                }

                return false
            }

            override fun describeTo(description: Description) {
                description.appendText("should return first matching item")
            }
        }
    }

    companion object {

        fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
            return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description) {
                    description.appendText("has item at position $position: ")
                    itemMatcher.describeTo(description)
                }

                override fun matchesSafely(view: RecyclerView): Boolean {
                    val viewHolder = view.findViewHolderForAdapterPosition(position)
                            ?: // has no item on such position
                            return false
                    return itemMatcher.matches(viewHolder.itemView)
                }
            }
        }

        fun chooser(matcher: Matcher<Intent>): Matcher<Intent> {
            return allOf(hasAction(Intent.ACTION_CHOOSER), hasExtra(`is`(Intent.EXTRA_INTENT), matcher))
        }
    }
}
