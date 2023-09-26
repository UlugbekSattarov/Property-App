
# Mars Real Estate

### An app that lets you browse, purchase and put to sale properties on Mars 🪐

All of the properties are stored locally with the Room database, they are **not**
hosted on a remote server. 

## Features

	- Browse properties, filter and sort them
	- Mark property as favorite to find them easily ( this is saved on disk)
	- Swipe to delete favorites
	- Viewpager2 to see additional photos 📷
	- Add a new property to sell/rent it, and import a photo from the camera or
	the file system
	- If you take of photo with the app, it will be saved in the pictures folder
	- Share a property with a link, this link can be opened with the app
	to directly go to the property's page
	- Login/Logout with credentials stored in encrypted preferences
	- Biometric login (will log you in as a default user if the biometric authentication
	succeeds)
	- Payment flow with credit card form and recap ( successful purchase will
	only add the property to your favorites and send you a notification)
	- Night mode 🌙
	- Option to choose a larger font size for the app
	- Transitions between routes, various animations in the app

## Architecture

This app heavily relies on the **Android Jetpack** components, notably using 

* Fragments
* Navigation
* ViewModel and LiveData (databinding)
* Room
* Test environment


Most of the UI is also built with the help of [Material Components](https://github.com/material-components/material-components-android)

## Compatibility

| Data  | Value |
| ------------- |:-------------:|
| Language      | Kotlin     |
| Minimum APi Level      | 21     |
| compileSdkVersion      | 33     |
| targetSdkVersion      | 33     |

App tested on Android API levels **21**, **25**, **29** and with screens **4.65" 320dpi**, **5.0" 420dpi** and **6.18" 408dpi**

Unit tests and integration tests are present for some viewmodels and fragments

Since the app was made a few years ago, some functions it uses are now deprecated (notably in tests)

###### Original idea inspired from [Google codelabs](https://codelabs.developers.google.com/?cat=android)



