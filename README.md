# Landmark Remark Native Android App

Landmark Remark native Android app fulfills the backlogs below by integrating Google Map SDK and Firebase services. This app uses MVVM architecture to enable a clear flow making the code easier to maintain and extend in the future.

# Backlog
Below sub tasks are created to complete the requirements

### 1.  As a user (of the application) I can see my current location on a map
   - Implementation of UI design
	   - ExploreFragment
	   - Google MapView
   - Implementation of Working flow

### 2. As a user I can save a short note at my current location
   - Preparation/Study of Firebase Realtime DB services
   - Implementation of UI design
	   - AddLocationNoteActivity : The reasons why adding a location note is an activity level component instead of a fragment in MainActivity are:
		   1. Reduce the complexity of  MainActivity
		   2. Decouple working flow for further development
   - Implementation of Working flow
   - Integration of DB relevant APIs

### 3. As a user I can see notes that I have saved at the location they were saved on the map
- Implementation of UI design
	- My Notes screen: CollectionsAdapter
- Implementation of Working flow
- Integration of DB APIs
	- Fetch noted locations based on creatorId

### 4. As a user I can see the location, text, and user-name of notes other users have saved
- Implementation of UI design
	- Other user's location notes will be presented on ExploreFragment
	- Location info view to provide detail location info when user click on a locaton marker
- Implementation of Working flow
- Integration of get note API

### 5. As a user I have the ability to search for a note based on contained text or user-name
- Implementation of UI design
	- Update ExploreFragment: add SearchView + ResultAdapter
- Implementation of Working flow
	- Implement functions to search
	- Filter results by creator name,  note visibility, contained text in note title and description.
- Integration of search note API(search note based on key words, user name)

# Integration of Cloud Services

Among the BaaS providers below

       Firebase ( https://firebase.google.com )
       Kumulos ( https://www.kumulos.com )
	   Realm ( https://realm.io/products/realm-mobile-platform)

I choosed Firebase as my backend service because I used Firebase Messaging and Analystics before, it looks more familar to me though I have not used their DB and Auth service. So to setup and integrate the Firebase service for this project, there will be some learning curve there but should not be an issue if time permits.

### 1.  Map service: Google Maps API
Enabled Google Maps SDK for Android on Google developer console
Generate and restrict the API key as mentioned (https://developers.google.com/maps/documentation/javascript/get-api-key) for Maps Javascript and Direction API

### 2. User Management: Firebase Auth
Preparation of Map Services
	   - Create Google Project and enable Google Map API
   - Preparation of Firebace project
	   - Auth - [https://firebase.google.com/docs/auth/android/custom-auth#kotlin+ktx_1](https://firebase.google.com/docs/auth/android/custom-auth#kotlin+ktx_1)
	   - Realtime Database - [https://firebase.google.com/docs/database/android/start](https://firebase.google.com/docs/database/android/start)
### 3. DB: Firebase real time DB
Data scheme of a locationData:

	   {
	       title: String
	       description: String
	       createdTime: String [UTC timeStamp, app will convert it to Date time]
	       creatorId: String [hash of email+encryption]
	       creatorName: String [ can be user's email or user name]
	       lat: Double [from -90 to 90]
	       lng: Double [from -180 to 180]
	       tags: Object [list of Strings, for latter extension]
	       extra: String [for later extension use]
	       visibility: String [To indicate who can view this note: public, private, friendOnly(has not implemented yet)]
	       imageUrl: String [for later extension]
	   }

# App structure
the diagram below shows overview of the project structure
![project structure](https://i.imgur.com/YiecPDK.png)


# App flow

### 0. User sign up/ sing in
       After successful sign in, user will be navigated to MainActivity and its default fragment: ExploreFragment will be showing.

### 1.  As a user (of the application) I can see my current location on a map
   - By default, user's current or last known location will be used as default location for ExploreFragment
   - User can tap on map view where has no location note then app will present a location information view suggesting there is no location note at the selected location. to add a new location note.
     From "Add Note" in the location information view, user can add new location note.

### 2. As a user I can save a short note at my current location
   - With the location that user picked in 1. the LatLng and address of the location will be passed to AddLocationNoteActivity.
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
   - When searched results are presented, user is not able to swipe or drag to move the map because it holds the focus.

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

# ToDos
1. Firebase Auth instance can be extracted to repository level
2. Add image for a location note



