# Greenfield Template (RxJava 2 version)

[![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=595a5011982d060001a36b8a&branch=master&build=latest)](https://dashboard.buddybuild.com/apps/595a5011982d060001a36b8a/build/latest?branch=master)

This is a template that I use for new projects. It already has some basic
stuff setup, such as:

* Retrofit + OKHttp
* RxJava
* Dagger
* MVP stuff
* Butterknife
* Local JUnit tests
* UI tests

This is a (more recent and less battle-tested) variation of the architecture decribed in
 my [blog post](http://zenandroid.io/testable-and-robust-architecture-for-android-projects/).
 The main difference here is the use of RxJava 2 instead of the event bus to bring back results
 from the service into the presenter. This does simplify a lot of the piping, particularly in
 the error handling part.
 
 > __Note:__ this does make extensive use of Lambda Expressions and thus requires Android Studio 3.0, which at the time of this writing is
 still in "Preview" state (available on the Canary channel). This is bound to change in the following weeks and the release is already
 quite stable in my experience. If that is an issue for you, please consider having a look at Retrolabda.

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
