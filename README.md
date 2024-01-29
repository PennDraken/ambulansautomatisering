# Ambulansautomatisering

For course ENM156 at Chalmers.

## Overview

The program is an Android Studio application that aims to automate the manual registration of different timestamps during an ambulance mission.  
The program works by using GPS-data to estimate where different timestamps might occur. Currently some of the GPS-locations are hard coded, and would be need to synced with mission information in a functional prototype. The timestamp "arrival at patient address" should instead automatically use the address provided to the ambulance personell. The timestamp, "arrival at hospital" also uses a GPS-coordinate which mich not correspond to the correct address. One possible implementation might be to use a list of known hospitals and their corresponding GPS-coordinates instead.

## Overall structure

MainActivity.java
1. Package and Imports:  
   -The code is part of the com.example.ambulansautomatisering package.  
   -Various Android and Google Play services packages are imported.  

2. Class Definition:  
   -MainActivity extends AppCompatActivity and implements LocationHelper.LocationListener.  

3. Member Variables:  
   -REQUEST_LOCATION_PERMISSION: Constant representing a request code for location permission.  
   locationHelper: An instance of the LocationHelper class.  

4. onCreate Method:  
   -Initializes the activity when it is first created.  
   -Sets the content view to the layout defined in activity_main.xml.  
   -Creates an instance of LocationHelper.  
   -Checks for location permissions:  
   -If permission is not granted, requests it.  
   -If permission is granted, starts location updates using locationHelper.startLocationUpdates().  

5. onResume Method:  
   -Called when the activity is resumed.  
   -Ensures that location updates are started.  

6. onPause Method:  
   -Called when the activity is paused.  
   -Stops location updates to conserve resources.  

7. onLocationChanged Method:  
   -Implemented from the LocationHelper.LocationListener interface.  
   -Handles the updated location by extracting latitude and longitude.  
   -Updates a TextView (locationTextView) with the new location information.  

--------------------------------------------------------------------------------------------
LocationHelper.java  
1. Class Definition:  
   -LocationHelper class responsible for handling location-related tasks.  

2. Member Variables:  
   -fusedLocationProviderClient: An instance of FusedLocationProviderClient from Google Play services.  
   -locationCallback: An instance of LocationCallback to handle location updates.  
   -locationListener: An interface for the callback when the location changes.  

3. Constructor:  
   -Initializes the FusedLocationProviderClient, locationListener, and calls createLocationCallback().  

4. createLocationCallback Method:  
   -Defines the behavior of the LocationCallback.  
   -When a new location result is received, it notifies the registered LocationListener.  

5. startLocationUpdates Method:  
   -Requests location updates from the FusedLocationProviderClient.  
   -Specifies update intervals, accuracy, and sets the callback.  
   -Handles security exceptions related to location access.  

6. stopLocationUpdates Method:  
   -Stops location updates by removing the callback.  

This code essentially sets up a location-aware Android app.
It checks for location permissions, starts location updates when granted, and updates a TextView with the current location.
The LocationHelper class abstracts away the details of location updates.
