package io.zenandroid.greenfield.feed

import android.graphics.Bitmap
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Single
import io.zenandroid.greenfield.model.Image
import io.zenandroid.greenfield.model.ImageListResponse
import io.zenandroid.greenfield.service.FlickrService
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.isNull
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.MockitoAnnotations
import java.io.InputStreamReader

/**
 * Created by alex on 27/01/2018.
 */

class FeedPresenterTest {

    @Mock
    lateinit var view: FeedContract.View

    @Mock
    lateinit var service: FlickrService

    @Mock
    lateinit var mockedBitmap: Bitmap

    private lateinit var presenter: FeedPresenter
    private lateinit var fakeResponse: ImageListResponse

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = FeedPresenter(view, service)

        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        fakeResponse = gson.fromJson(
                InputStreamReader(javaClass.classLoader!!.getResourceAsStream("mock_data.json")),
                ImageListResponse::class.java
        ).also {
            `when`(service.getImageList(any())).thenReturn(Single.just(it))
        }


    }

    @Test
    fun whenPresenterIsSubscribed_thenImagesAreLoaded() {

        val expected = getListSortedByPublishedDate(fakeResponse.items!!)

        presenter.subscribe()

        verify(view).showProgressDialog()
        verify(service).getImageList(isNull())

        verify(view).showImages(expected)

        verify(view).dismissProgressDialog()

    }

    @Test
    fun whenTagsAreEntered_thenImagesWithTagsAreLoaded() {
        presenter.onSearch("cat dog house")

        verify(view).showProgressDialog()
        verify(service).getImageList("cat,dog,house")
        verify(view).showImages(anyList())
        verify(view).dismissProgressDialog()

        reset(view, service)
        `when`(service.getImageList(any())).thenReturn(Single.just(fakeResponse))

        presenter.onSearch("cat dog")
        verify(view).showProgressDialog()
        verify(service).getImageList("cat,dog")
        verify(view).showImages(anyList())
        verify(view).dismissProgressDialog()
    }

    @Test
    fun whenRefreshIsRequested_thenImagesAreFetchedAgain() {
        presenter.onRefresh()
        verify(view).showProgressDialog()
        verify(service).getImageList(isNull())

        verify(view).showImages(anyList())
        verify(view).dismissProgressDialog()
    }

    @Test
    fun whenOnSortIsCalledWithNewParameter_thenImagesAreSorted() {
        val expected = getListSortedByTakenDate(fakeResponse.items!!)
        presenter.onSortBy(FeedContract.SortCriterion.TAKEN)

        verify(view).showProgressDialog()
        verify(service).getImageList(isNull())

        verify(view).showImages(expected)

        verify(view).dismissProgressDialog()

        reset(view, service)
        presenter.onSortBy(FeedContract.SortCriterion.PUBLISHED)
        verify(view).showProgressDialog()
        verify(view).showImages(getListSortedByPublishedDate(fakeResponse.items!!))

        verify(view).dismissProgressDialog()

    }

    @Test
    fun whenOnSortIsCalledWithSameParameter_thenNothingHappens() {
        presenter.subscribe()

        reset(view, service)
        presenter.onSortBy(FeedContract.SortCriterion.PUBLISHED)

        verifyNoMoreInteractions(view, service)

    }

    @Test
    fun whenOnBrowseImageIsCalled_thenBrowserIsOpened() {
        presenter.onBrowseImage(fakeResponse.items!![0])
        verify(view).browseURL(fakeResponse.items!![0].link!!)
    }

    @Test
    fun whenOnSaveImageIsCalled_thenImageIsSaved() {
        val captor = argumentCaptor<Target>()
        presenter.onSaveImage(fakeResponse.items!![0])
        verify(view).loadImageFromURL(anyString(), captor.capture())

        captor.firstValue.onBitmapLoaded(mockedBitmap, Picasso.LoadedFrom.DISK)
        verify(view).saveBitmapToGallery(com.nhaarman.mockitokotlin2.eq(mockedBitmap), anyString(), anyString())
    }

    @Test
    fun whenOnShareIsCalled_thenImageIsShared() {
        presenter.onShareImage(fakeResponse.items!![0])
        verify(view).sendEmail(fakeResponse.items!![0].media!!.m!!)
    }

    private fun getListSortedByPublishedDate(original: List<Image>) =
            original.sortedBy { it.published }

    private fun getListSortedByTakenDate(original: List<Image>) =
            original.sortedBy { it.dateTaken }

}
