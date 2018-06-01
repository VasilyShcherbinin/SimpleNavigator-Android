COM1032 Mobile Computing 
Assignment 2

Student Name: 		Vasily Shcherbinin
Student Email: 		vs00162@surrey.ac.uk
Application Name:	vs00162_assignment2
Timestamp:		05:27 24/05/2016

My application fulfills all the core features set in the problem definition:

	1) App makes clear use of multiple activities and entirely appropriate use of implicit intents where appropriate.
	2) App displays clearly in landscapre with use of a separate landscape layout xml file (MainActivity)*.

	*The MapActivity screen orientation is set permanently to portrait in the Manifest. 
	 This is not a bug, but is deliberate. 
	 Other activities do support screen rotation.

	3) The app utilises an SQLite Database to store markers from the map.
	4) Care was taken to produce readable code with good style for validation.
	5) Care was taken to produce an intuitive, user-friendly UI for the app, with information being updated and displayed in real time.
	6) The app is of a stable build and does not crash under normal circumstances, as well did not crash during stress testing. 
	7) The application makes use of fragments. 
	8) Services were used extensively throughout the application.
	9) An attempt was made to utilize asynchronous tasks. 
	10) An SQLite database for map markers was implemented using Content Providers. 
	11) Great effort was made to provide an excellent interactive use of location awareness to display on the UI. 
	12) Several advanced features were attempted and implemented for the application.

	

Following point 12) the following advanced requirements/interesting implementations using location awareness have been attempted and successfully implemented:

	1) Calculating path between two points adhering to roads.
	2) Calculate distance for the path mentioned above.
	3) Calculate travel time by car for the path mentioned above.
	4) Finding address using PlacePicker from Google Places API.

The following API have been used in the creation of this application:

	1) Google Maps Android API
	2) Google Maps Directions API
	3) Google Places API for Android

In addition to the said above, this application contains features:

	1) A Splashscreen that runs during bootup. Implemented as per Google recommendations.
	2) An Icon for the application has been created. 
	
Special instructions:

	1) In order to see the Splashscreen, the application must be booted from a portrait position. 

Finally, all code has been tested using AVD and the ASUS tablet, as well as was correctly formatted in Android Studio. 

References:

Mathew, G. (2013) Storing and retrieving locations in SQLite from Google maps Android API V2. Available at: http://wptrafficanalyzer.in/blog/storing-and-retrieving-locations-in-sqlite-from-google-maps-android-api-v2/#comment-255841 (Accessed: 24 May 2016).

Sambells, J. (2010) Decoding Polylines from Google maps direction API with java. Available at: http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java (Accessed: 24 May 2016).