# Greenfield Template (RxJava 2 version)

[![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=595a5011982d060001a36b8a&branch=master&build=latest)](https://dashboard.buddybuild.com/apps/595a5011982d060001a36b8a/build/latest?branch=master)
[![Build status](https://build.appcenter.ms/v0.1/apps/c673f10e-0551-4819-b539-07f767f0f4fe/branches/master/badge)](https://appcenter.ms)

This is a template that I use for new projects. It already has some basic stuff setup, such as:

* Retrofit + OKHttp
* RxJava2
* Dagger2
* MVP architecture
* Kotlin extensions
* JUnit tests (with Mockito)
* UI tests with Espresso (run on mockDebug variant!)
* Mock and connected variants (for offline work and tests)
* Permission dispatcher library
* CI Integration (Microsoft's AppCenter) that runs the tests on commit

The app itself connects to Flickr and displays a list of images. You can search for
particular tags or you can sort the images.

TODO:
* maybe have an MVI version?
* try out mosby for MVP boilerplate?
* maybe do some cool stuff with Groupie?
* Koin instead of Dagger?
* Move more towards Clean Architecture.

Note: I wrote a [blog post](http://zenandroid.io/testable-and-robust-architecture-for-android-projects/) about
an old version of this architecture. It is now obsolete, but the article might be interesting
for historic reasons. Remember EventBus? :)

## Overview

Here is the diagram of the architecture proposed here:

![](https://cdn.rawgit.com/acristescu/GreenfieldTemplate/86c6e7a/architecture.svg)

The flow of events and data:

1. The __Activity__ reacts to the user input by informing the __Presenter__ that data is required.
1. The __Presenter__ fires off the appropriate request in the service layer (and instructs the __Activity__ to display a busy indicator).
1. The __Service__ then issues the correct REST call to the __Retrofit__ layer.
1. The __Retrofit__ layer exchanges HTTP requests and responses with the Server and returns an `Observable` (or perhaps `Single`).
1. The __Service__ possibly inspects the `Observable`, schedules it on the correct thread (in order to keep the __Presenter__ free of Android Schedulers and thus pure JUnit testable) and returns it back to the __Presenter__.
1. The __Presenter__ receives the `Observable`, retrieves the data or error and issues the correct commands to the __Activity__ to update the UI (and dismiss the busy indicator).
1. The __Activity__ presents the user with the data or error message.
