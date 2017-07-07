# Greenfield Template

[![BuddyBuild](https://dashboard.buddybuild.com/api/statusImage?appID=595a5011982d060001a36b8a&branch=master&build=latest)](https://dashboard.buddybuild.com/apps/595a5011982d060001a36b8a/build/latest?branch=master)

This is a template that I use for new projects. It already has some basic
stuff setup, such as:

* Retrofit + OKHttp
* Otto
* Dagger
* MVP stuff
* Butterknife
* Local JUnit tests
* UI tests

For more info, you can check out the [blog post](http://zenandroid.io/testable-and-robust-architecture-for-android-projects/) I wrote about it.

## Overview

Here is the diagram of the architecture proposed here:

![](https://cdn.rawgit.com/acristescu/GreenfieldTemplate/master/architecture.svg)

The flow of events and data:

1. The __Activity__ reacts to the user input by informing the __Presenter__ that data is required.
1. The __Presenter__ fires off the appropriate request in the service layer (and instructs the __Activity__ to display a busy indicator).
1. The __Service__ then issues the correct REST call to the __Retrofit__ layer (providing a callback).
1. The __Retrofit__ layer exchanges HTTP requests and responses with the Server and then calls either the `onSuccess` or `onFailure` method (as the case may be) on the provided __Callback__.
1. The __Callback__ posts the data or error on the __Event Bus__.
1. The __Presenter__ (being subscribed the relevant data and errors on the __Event Bus__) receives the data or error and issues the correct commands to the __Activity__ to update the UI (and dismiss the busy indicator).
1. The __Activity__ presents the user with the data or error message.
