

# Landmark Remark Native Android App

Landmark Remark native Android app fulfills the backlogs below by integrating Google Map SDK and Firebase services. This app uses MVVM architecture to ensure a clear flow, allows ease of maintaining the code and scalability in the future.

# Backlog
Below sub-tasks are created to detail the efforts and steps taken to fulfill the requirements

### 1.  As a user (of the application) I can see my current location on a map
   - Implementation of UI design
      - ExploreFragment
      - Google MapView
   - Implementation of Working flow
      - Add Google Mapview to ExploreFragment
       - Map info, marker and gesture operation

### 2. As a user I can save a short note at my current location
   - Preparation/Study of Firebase Realtime DB services
   - Implementation of UI design
      - AddLocationNoteActivity : The reasons why adding a location note is an activity level component instead of a fragment in MainActivity are:
         1. Reduce the complexity of  MainActivity
         2. Decouple working flow for further development such as location update and multimedia support
   - Implementation of Working flow
   - Integration of Firebase DB relevant APIs

### 3. As a user I can see notes that I have saved at the location they were saved on the map
- Implementation of UI design
   - My Notes screen "Notes": CollectionFragment + CollectionsAdapter
     (The component of this screen is named "CollectionFragment" for further support of "Favorite" notes or "Friend's" note)
- Implementation of Working flow
- Integration of DB APIs
   - Fetch noted locations based on creatorId

### 4. As a user I can see the location, text, and user-name of notes other users have saved
- Implementation of UI design
   - Update Location Info view
- Implementation of Working flow
   - Other user's location notes will be presented on ExploreFragment
   - Location info view to provide detail location info when user click on a location marker
- Integration of get note API

### 5. As a user I have the ability to search for a note based on contained text or user-name
- Implementation of UI design
   - Update ExploreFragment: add SearchView + ResultAdapter
- Implementation of Working flow
   - Implement functions to search location notes
   - Filter results by creator name,  note visibility, contained text in note title and description.
- Integration of search note API(search note based on key words, user name)

# Integration of Cloud Services

Among the BaaS providers below

      Firebase ( https://firebase.google.com )
      Kumulos ( https://www.kumulos.com )
      Realm ( https://realm.io/products/realm-mobile-platform)

I chose **Firebase** as my backend service because I have used Firebase Messaging and Analytics before, it is a familiar service for me, although I have not implemented Realtime DB and Auth services in the past, which are required for this app. I spent some time studying the SDK on how to setup and integrate Firebase service, it was not difficult for me to understand and be able to use the service.


### User Management: Firebase Auth
   - Preparation of Firebase Auth - [https://firebase.google.com/docs/auth/android/custom-auth#kotlin+ktx_1](https://firebase.google.com/docs/auth/android/custom-auth#kotlin+ktx_1)
- Regarding the user authentication, we only support "Email" + "Password" to sign up and sign in.    [https://firebase.google.com/docs/auth/android/password-auth](https://firebase.google.com/docs/auth/android/password-auth)
- Although Firebase Auth supports other approaches such as Google sign-in, Facebook Login, and etc, the reason why I chose Email + Password for user authentication is because when I started, I used "Email + Password" as custom user id to manage location note data as I thought there would be limited time to integrate a user Auth service. However, later I realised it is not hard to integrate Firebase Auth and its [passwordAuthentication](https://firebase.google.com/docs/auth/android/password-auth#create_a_password-based_account) API can meets my original design so I move on this approach.
-  At the moment, user sign up doesn't require email verification, user can register an account if the the email is not in use by other users.
- After user signs in sucessfully, the app will update the user's profile with Firebase Auth API and the user name used during sign up will be updated.

###  DB: Firebase real time DB
- Preparation of Realtime Database - [https://firebase.google.com/docs/database/android/start](https://firebase.google.com/docs/database/android/start)
Data scheme of a locationData:

      {
          title: String
          description: String
          createdTime: String [UTC timeStamp, app will convert it to Date time]
          creatorId: String [hash of email+encryption]
          creatorName: String [ can be user's email or user name]
          lat: Double [from -90 to 90]
          lng: Double [from -180 to 180]
          tags: Object [list of Strings, for later extension]
          extra: String [for later extension]
          visibility: String [To indicate who can view this note: public, private, **friendOnly(has not implemented yet)**]
          imageUrl: String [for later extension]
      }

  ### 3.  Map service: Google Maps API
  Before integrating Google Maps to the app, here are some pre-integrations to go through.
-  Create Google Project
-  Enabled Google Maps SDK for Android on Google developer console
Generate and restrict the API key as mentioned (https://developers.google.com/maps/documentation/android-sdk/start#step_4_set_up_a_google_maps_api_key)
- With the Google API key above, we can start working on MapView in the app
# App structure
the diagram below shows overview of the project structure
![project structure](https://i.imgur.com/YiecPDK.png)


App components under MVVM architecture

      [View]  ----------------------> [ViewModel] -----------------------[Model]
         |    <----------------------      |      -----------------------   |
      MainActivity              MainViewModel                Firebase realtime DB
      ExploreFragment
      CollectionsFragment
      ProfileFragment

# App flow

### 0. User sign up/ sign in
   - After successful sign in, user will be navigated to MainActivity and its default fragment: ExploreFragment will be showing.

### 1.  As a user (of the application) I can see my current location on a map
   - By default, user's current or last known location will be used as default location for ExploreFragment


### 2. As a user I can save a short note at my current location
  - User can tap on map view where has no location note. A prompt will be shown on the map and allow the user to add a new note.
     User can add a new location note from the "Add Note" in the location information view,
   - With the location that user selects, the LatLng and address of the location will be passed to AddLocationNoteActivity.
   - AddLocationNoteActivity allows user to add "title", "description" and visibility of the editing note. currently the app can only support "public" and "private".

### 3. As a user I can see notes that I have saved at the location they were saved on the map
   - Tap "Notes" in the bottom navigation bar will navigate to CollectionFragment.
   - At CollectionFragment page, user can browse the notes which the user have created before.

### 4. As a user I can see the location, text, and user-name of notes other users have saved
   - "Location information view" in ExploreFragment presents the content of a location note
   - Location notes which are owned by the user or other users are visible if the visibility of the location note is "public"
   - Tapping marker in map view and searched items in ExploreFragment will bring up location information view
   - Map makers: Green markers are location notes created by current user. Orange markers indicate location notes created by other users.

### 5. As a user I have the ability to search for a note based on contained text or user-name
   - SearchView in Fragment provides the function to search notes by keyword in the view.
   - If keyword matches title of a location note or it is included in the note description or keyword matches note creator's name, the location note will be added to searched list.
   - The floating button at the right bottom in ExploreFragment can toggle the visibility of the SearchView
   - When search results are presented, user is not able to swipe or drag to move the map because it holds the focus.

### Extra
  - User can tap "Me" in the bottom navigation bar to navigate to ProfileFragment
  - ProfileFragment shows the user's display name which are registered during sign up process
  - ProfileFragment shows the email which user signs in
  - User can sign out by tapping "Sign out" in ProfileFragment

# Time Spent
Hours spent on implementing the app:
- Integration of Firebase + study - 2 hrs
- App Architecture & UX Design - 2 hrs
- Implementation - 12 hrs
- Tuning + bug fix - 4 hrs


# Known Issues
1. Location info doesn't present after creating a new location note.
2. User management flow needs refinement
3. User cannot create location note after sign out/sign in with another account, restart app will fix this issue.

# ToDos
Below are points to improve the UX/performance of this app
1. Firebase Auth instance can be extracted to repository level
2. Support image/multi-media for a location note
3. Support location note Update/Delete
4. Add "like" count to location note
5. Support updating user's profile
6. Add "home" icon in top bar in ExploreFragment to allow user to go back to their current location again