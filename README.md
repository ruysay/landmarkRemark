# LandmarkRemark Native Android App

Hi! I'm your first Markdown file in **StackEdit**. If you want to learn about StackEdit, you can read me. If you want to play with Markdown, you can edit me. Once you have finished with me, you can create new files by opening the **file explorer** on the left corner of the navigation bar.


# Backlog

StackEdit stores your files in your browser, which means all your files are automatically saved locally and are accessible **offline!**

### 1.  As a user (of the application) I can see my current location on a map (Product backlog)
   - Preparation of Map Services 
	   - Create Google Project and enable Google Map API
   - Implementation of UI design
	   - SearchFragment
   - Implementation of Working flow

### 2. As a user I can save a short note at my current location
   - Preparation of DB services
   - Implementation of UI design (The idea of using similar task naming skill is to provide non-tech people to have, people will know a similar task should contain all these tasks)
   - Implementation of Working flow
   - Integration of save note API

### 3. As a user I can see notes that I have saved at the location they were saved on the map
- Implementation of UI design
- Implementation of Working flow
- Integration of get note API(get note based on userId)

### 4. As a user I can see the location, text, and user-name of notes other users have saved
- Implementation of UI design
- Implementation of Working flow
- Integration of get note API(get note based on userId)

### 5. As a user I have the ability to search for a note based on contained text or user-name
- Implementation of UI design
- Implementation of Working flow
- Integration of search note API(search note based on key words)

# Components

The file explorer is accessible using the button in left corner of the navigation bar. You can create a new file by clicking the **New file** button in the file explorer. You can also create folders by clicking the **New folder** button.

### 1.  Map service: Google Maps API
Enabled Google Maps SDK for Android on Google developer console
Generate and restrict the API key as mentioned (https://developers.google.com/maps/documentation/javascript/get-api-key) for Maps Javascript and Direction API

### DB: Firebase real time DB
Data scheme of a location:

	   {
	       title: String
	       description: String
	       createdTime: String
	       creatorId: String [hash of email+encryption]
	       lat: float [from -90 to 90]
	       long: float [from -180 to 180]
	       tags: object [list of Strings]
	       extra: String [for later extension use]
	       visibility: String [To indicate who can view this note: public, private, friendOnly]
	       imageUrl: String [for later extension]
	   }



# App Architecture
### One Activity App
### MVVM
### One Activity App
### Firebase Realtime DB


# Implementation details

# ToDos