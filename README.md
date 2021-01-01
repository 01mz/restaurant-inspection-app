# Restaurant Inspection App

 Android app to browse restaurant health inspection in the Surrey region. 
 The app fetches inspection data from City of Surrey's Open Data API and displays it in a scrollable list view or a map view using Google Maps API.

<!-- USAGE EXAMPLES -->
## Screenshots

![permissions.jpg](images/permissions.jpg)
![mapview_zoomedout](images/mapview_zoomedout.jpg)
![mapview_zoomedin](images/mapview_zoomedin.jpg)
![mapview_singlerestaurant](images/maview_singlerestaurant.jpg)


![listview_filtered.jpg](images/listview_filtered.jpg)
![listview_7eleven.jpg](images/listview_7eleven.jpg)

![restaurant.jpg](images/restaurant.jpg)

![filter_changed.jpg](images/filter_changed.jpg)

![update_available2.jpg](images/update_available2.jpg)
![downloading.jpg](images/downloading.jpg)



## Installation
1. Clone this repository and import into **Android Studio**
   ```bash
   git clone https://github.com/01mz/restaurant-inspection-app.git
   ```
2. Use your own Google API key in `res/values/google_maps_api.xml`. 
   ```
   <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR_API_KEY</string>
   ```
   You must have the Google Maps Android API enabled for your API key.

