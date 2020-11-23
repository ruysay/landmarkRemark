# Landmark Remark Native Android App

Landmark Remak native Android app fulfills the backlogs below by integrating Google Map SDK and Firebase services. This app uses MVVM architecture to enable a clear flow making the code easier to maintain and extend in the future.

# Backlog
The backlog is divided as sub tasks below.
The idea of using similar task naming is to provide non-tech people to have,
people will know a similar task should contain all these tasks

### 1.  As a user (of the application) I can see my current location on a map (Product backlog)
   - Implementation of UI design
	   - ExploreFragment
	   - Google MapView
   - Implementation of Working flow

### 2. As a user I can save a short note at my current location
   - Preparation of DB services
   - Implementation of UI design
	   - AddNoteActivity
   - Implementation of Working flow
   - Integration of DB relevant APIs

### 3. As a user I can see notes that I have saved at the location they were saved on the map
- Implementation of UI design
	- My Notes screen
- Implementation of Working flow
- Integration of DB APIs
	- Fetch noted locations based on creatorId

### 4. As a user I can see the location, text, and user-name of notes other users have saved
- Implementation of UI design
	- Other user's location notes will be presented on ExploreFragment
	- Location Info view to provide detail location info when user click on a locaton marker
	-
- Implementation of Working flow
- Integration of get note API(get note based on userId)

### 5. As a user I have the ability to search for a note based on contained text or user-name
- Implementation of UI design
- Implementation of Working flow
- Integration of search note API(search note based on key words)

# Integration of Cloud Services

Amongs the BaaS providers below

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
	       creatorName: String [user's email]
	       lat: Double [from -90 to 90]
	       lng: Double [from -180 to 180]
	       tags: Object [list of Strings, for latter extension]
	       extra: String [for later extension use]
	       visibility: String [To indicate who can view this note: public, private, friendOnly(has not implemented yet)]
	       imageUrl: String [for later extension]
	   }



# App Architecture
### MVVM
    MainActivity ---- MainViewModel --- MainRepository


### Firebase Realtime DB


# Spent Time
In total, I spent about 17 - 18 hours working on this app.

Integration of Firebase + study - 2 hrs
App Design - 2 hrs
Implementation - 12 hrs
Tuning + bug fix - 2 hrs


# Implementation details

# Known Issues


# ToDos
