# Madrid Guide

This is a small shops and activities guide in the city of Madrid for Android 4.0.1+ devices.

When the app is first launched, all necessary data (about 70 Mb, images included) is downloaded and cached on the device, so no Internet connection will be necessary the next time it is launched. If the cached data is older than one week, it all will be replaced with an updated copy.

The user can check the shops/activities on an alphabetically sorted list or locate them geographically on the city map. A search bar is available on the screen top to filter results by the entered text, looking for matches on the item title or description. Both the list and the map update their results on every new search.

By clicking on a result, the user can visualize the detail of the selected shop/activity. This includes the address, localized description and opening hours, a link to the website (opened in external browser) and a zoomable static map image showing the item location and its surroundings.

The following external libraries are used:

- **Gson 2.8.0**: to parse the JSON data retrieved from the server.
- **Volley 1.0.0**: for all the http request managing (except images).
- **Picasso 2.5.2**: to manage the image download and caching.
- **OkHttp3 3.3.0** y **OkHttp3 Downloader 1.0.2**: to manage Picasso's disk cache.
- **Butter Knife 8.4.0**: to easily bind the layout views in the code.
- **Google Maps Service 10.0.0**: to show the shops/activities on a map.
- **PhotoView** 1.2.4: to turn the static map images into zoomable.


&nbsp;
### Screenshots:

&nbsp;
<kbd> <img alt="screenshot 1" src="https://cloud.githubusercontent.com/assets/18370149/26381036/66af8e4c-4022-11e7-8693-9390adad7c9a.png" width="256"> </kbd> &nbsp; <kbd> <img alt="screenshot 2" src="https://cloud.githubusercontent.com/assets/18370149/26381037/66c9a138-4022-11e7-88e5-503097e63c2d.png" width="256"> </kbd> &nbsp; <kbd> <img alt="screenshot 3" src="https://cloud.githubusercontent.com/assets/18370149/26381038/66d0c954-4022-11e7-94fb-2b2ff1f0e802.png" width="256"> </kbd>

&nbsp;
<kbd> <img alt="screenshot 4" src="https://cloud.githubusercontent.com/assets/18370149/26381039/66d565c2-4022-11e7-9ce9-63f32ac868e9.png" width="256"> </kbd> &nbsp; <kbd> <img alt="screenshot 5" src="https://cloud.githubusercontent.com/assets/18370149/26381040/66d66b7a-4022-11e7-8ec3-e405898c4175.png" width="256"> </kbd> &nbsp; <kbd> <img alt="screenshot 6" src="https://cloud.githubusercontent.com/assets/18370149/26381041/66d77f10-4022-11e7-99a4-30da5df7cdf9.png" width="256"> </kbd>

&nbsp;
<kbd> <img alt="screenshot 7" src="https://cloud.githubusercontent.com/assets/18370149/26381042/66d85f8e-4022-11e7-80c9-f35feeef9d2c.png" width="768"> </kbd>

&nbsp;
<kbd> <img alt="screenshot 8" src="https://cloud.githubusercontent.com/assets/18370149/26381043/66e4b162-4022-11e7-8279-a7027fe1d876.png" width="768"> </kbd>


&nbsp;
### Application architecture:

The app has been designed following the principles of a **Clean Architecture**, organizing it in several layers where the code dependencies point inwards only, and the inner layers do not know anything about the outer layers. This way a separation of concerns (presentation, businnes rules, data persistence, etc) is achieved while elements in each level can be tested relying on the tests of the previous level.

Also, each layer uses its own data model so that they are independent of the specific implementation used in the other layers, as long as the interfaces between them remain unchanged. You can find additional information about Clean Architectures <a href="https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html">**here**</a>.

Following is a description of the packages the app is organized into:

##### - **activity**:
These are the Activity classes acting as controllers for each screen in the application.

##### - **adapter**:
The Adapter classes to represent model objects on certain views like tables or maps.

##### - **fragment**:
The fragment controllers used for the shop and activity lists.

##### - **interactor**:
These are the intermediary classes between the controllers and the model. They are always invoked from classes in the activity package and implement the use cases of the application. All interactors use a delegate or listener who acts as a callback after the interactor finishes its operation (usually in the background).

##### - **manager**:
Here are the classes that disengage the interactors from the specific way operations are implemented. This classes have been implemented as singletons, and are organized into several sub-packages:

- **db**: classes that manage the local cache used to store data sent by the server, a SQLite database in the current version. The main class in the package is *DBManager*, who centralizes the accesses to the database. This class, however, is not directly accessed by the interactors. Objects implementing *DAOPersistable* (Data Access Object) act as the visible interface for DBManager, since they expose only the operations the interactors need to invoke. The class *MadridGuideProvider* in the subpackage **provider** is a **Content Provider**, a standard Android data access interface that exposes the application data through URIs and Loaders. This way data can be accessed both from within the application and from external applications, if needed.

- *image*: contains the *ImageCacheManager* wich is in charge of downloading and caching the remote images.

- *net*: contains the *NetworkManager* class in charge of sending Http requests and parsing the JSON responses, among other responsibilities like detecting the device connection type. This package also includes the auxiliary clases describing the remote JSON responses, necessary for the parsing process.

   
##### - **model**:
This package groups the main application model objects and the aggregates of this classes, as well as the interfaces implemented by them.

There is a sub-package **mapper** with utility classes used to map the classes representing JSON objects into the model classes.

##### - **navigator**:
Here is the *Navigator* class, where all transitions between the application Activities are centralized.

##### - **view**:
Groups utility classes to manage certain views, like the ViewHolders of the RecyclerViews. Also the class *OnElementClickedListener*, which implements a generic interface to listen to user taps on some views like the rows of a RecyclerView.

##### - **util**:
This package contains the *Utils* class with auxiliary common methods to manage the UI, and the *MainThread* class that simplifies the execution of code blocks in the foreground from background threads.

Also, the class *Constants* belongs in this package. It just stores the application settings like the remote server URLs, the disk cache size, the cache expiration limit or the initial area to show when maps are loaded.


&nbsp;
### Additional considerations:

- To avoid confusion between the Android *Activity* class and the activity model entities, the latter has been named 'Experience'.

- The repository does not include an API key to use the **Google Maps for Android** services. In order to complile the project, it is mandatory to generate a new key from the <a href="https://console.developers.google.com">Google Developer console</a> and create a new file **/res/values/api_keys.xml** with the following content:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="google_maps_api_key">PUT YOUR API KEY HERE</string>
</resources>
```

- The interactor class *CacheAllImagesInteractor* builds an http request queue to download and cache the images for the shops and the activities. This queue is processed by the *ImageCacheManager* in the background, where the requests are launched concurrently (Picasso's limit is 3 downloads at the same time) to save time. Control to the main thread is not returned until all the requests have finished.

- The method *InfoWindowAdapter.refreshInfoWindow()* is invoked after loading the image inside the Info Window of a map marker, and all it does is to hide the Info Window and showing it again immediately after. This is necessary because sometimes the image is not memory cached and needs to be retrieved from disk, wich takes a bit longer. The Info Window is a special view wich is rendered as a bitmap right after the map marker is clicked, and the disk cached images have no time to be loaded before the view renders. Invoking refreshInfoWindow() once the image is loaded into memory allows it to show in the Info Window.

- The search criteria to filter shops/activities by the upper search bar is to match the entities where the search string is found in the name, description (in the localized language only) or address fields. All searches (including the initial search with no filters) are performed by a **Loader** wich is given the filtering data.

- The ShopsActivity and ExperiencesActivity activities have two different layouts: one for portrait orientation (map up, list down) and other for landscape orientation (map on the left, list on the right). To keep the on screen information when rotating the device, both activities implement the onSaveInstanceState() method that saves the necessary data (the list of entities being shown and the current map position). The saved data, if any, is restored in the onCreate() method.

- All text literals in the application are available in Spanish and English, and will show depending on the system language (being English the default choice). Regarding the shops and activities data, both the descriptions and the opening hours are available in these two languages. When some of these fields needs to be shown, the appropriate choice is determined in runtime by invoking the **getLocalizedDescription()** or **getLocalizedOpeningHours()** methods of the entity.
