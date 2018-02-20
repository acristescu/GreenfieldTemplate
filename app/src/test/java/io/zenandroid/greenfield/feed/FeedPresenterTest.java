package io.zenandroid.greenfield.feed;

import android.graphics.Bitmap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Target;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.zenandroid.greenfield.model.Image;
import io.zenandroid.greenfield.model.ImageListResponse;
import io.zenandroid.greenfield.service.FlickrService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by alex on 27/01/2018.
 */

public class FeedPresenterTest {

    @Mock FeedContract.View view;

    @Mock FlickrService service;

    @Mock Bitmap mockedBitmap;

    private FeedPresenter presenter;
    private ImageListResponse fakeResponse;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        presenter = new FeedPresenter(view, service);

        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        fakeResponse = gson.fromJson(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("mock_data.json")),
                ImageListResponse.class
        );

        when(service.getImageList(any())).thenReturn(Single.just(fakeResponse));
    }

    @Test
    public void whenPresenterIsSubscribed_thenImagesAreLoaded() {

        final List<Image> expected = getListSortedByPublishedDate(fakeResponse.getItems());

        presenter.subscribe();

        verify(view).showProgressDialog();
        verify(service).getImageList(isNull());

        verify(view).showImages(expected);

        verify(view).dismissProgressDialog();

    }

    @Test
    public void whenTagsAreEntered_thenImagesWithTagsAreLoaded() {
        presenter.onSearch("cat dog house");

        verify(view).showProgressDialog();
        verify(service).getImageList("cat,dog,house");
        verify(view).showImages(anyList());
        verify(view).dismissProgressDialog();

        reset(view, service);
        when(service.getImageList(any())).thenReturn(Single.just(fakeResponse));

        presenter.onSearch("cat dog");
        verify(view).showProgressDialog();
        verify(service).getImageList("cat,dog");
        verify(view).showImages(anyList());
        verify(view).dismissProgressDialog();
    }

    @Test
    public void whenRefreshIsRequested_thenImagesAreFetchedAgain() {
        presenter.onRefresh();
        verify(view).showProgressDialog();
        verify(service).getImageList(isNull());

        verify(view).showImages(anyList());
        verify(view).dismissProgressDialog();
    }

    @Test
    public void whenOnSortIsCalledWithNewParameter_thenImagesAreSorted() {
        final List<Image> expected = getListSortedByTakenDate(fakeResponse.getItems());
        presenter.onSortBy(FeedContract.SortCriterion.TAKEN);

        verify(view).showProgressDialog();
        verify(service).getImageList(isNull());

        verify(view).showImages(expected);

        verify(view).dismissProgressDialog();

        reset(view, service);
        presenter.onSortBy(FeedContract.SortCriterion.PUBLISHED);
        verify(view).showProgressDialog();
        verify(view).showImages(getListSortedByPublishedDate(fakeResponse.getItems()));

        verify(view).dismissProgressDialog();

    }

    @Test
    public void whenOnSortIsCalledWithSameParameter_thenNothingHappens() {
        presenter.subscribe();

        reset(view, service);
        presenter.onSortBy(FeedContract.SortCriterion.PUBLISHED);

        verifyNoMoreInteractions(view, service);

    }

    @Test
    public void whenOnBrowseImageIsCalled_thenBrowserIsOpened() {
        presenter.onBrowseImage(fakeResponse.getItems().get(0));
        verify(view).browseURL(fakeResponse.getItems().get(0).getLink());
    }

    @Test
    public void whenOnSaveImageIsCalled_thenImageIsSaved() {
        final ArgumentCaptor<Target> captor = ArgumentCaptor.forClass(Target.class);
        presenter.onSaveImage(fakeResponse.getItems().get(0));
        verify(view).loadImageFromURL(anyString(), captor.capture());

        captor.getValue().onBitmapLoaded(mockedBitmap, null);
        verify(view).saveBitmapToGallery(eq(mockedBitmap), anyString(), anyString());
    }

    @Test
    public void whenOnShareIsCalled_thenImageIsShared() {
        presenter.onShareImage(fakeResponse.getItems().get(0));
        verify(view).sendEmail(fakeResponse.getItems().get(0).getMedia().getM());
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
}
