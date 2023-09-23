# CarZone
CarZone is an Android Application like OLX app for buy and sell cars

<img src="https://github.com/ahmedkhalifa14/ShopApp/assets/87338764/cc2c83a0-dc2d-48fd-b151-511a85528911" width ="350" height="250">

# App Architecture
CarZone follows uncle pop clean architecture
"Uncle Pop Clean Architecture" is an app that exemplifies the principles of clean architecture in software development. It is designed with a clear separation of concerns, ensuring that business logic, user interface, and data access layers are distinct and independent. This architectural approach enhances maintainability, scalability, and testability. Uncle Pop Clean Architecture places a strong emphasis on modularity, making it easier to add or modify features without affecting the entire codebase. This app is a testament to best practices in software development, promoting clean, readable, and maintainable code that ensures a robust and adaptable foundation for future enhancements and improvements.

<img src="https://github.com/ahmedkhalifa14/ShopApp/assets/87338764/de5690e7-6532-4cd2-a501-64752a746b7e" width ="350" height="250">

CarZone App Divided into three Layers 
ui layer, data layer and domain layer

<img src="https://github.com/ahmedkhalifa14/ShopApp/assets/87338764/133967e9-4399-4cfe-a4ec-21950802f12d" width ="350" height="250">

UI layer

The role of the UI layer (or presentation layer) is to display the application data on the screen. Whenever the data changes, either due to user interaction (such as pressing a button) or external input (such as a network response), the UI should update to reflect the changes.

Data layer

The data layer of an app contains the business logic. The business logic is what gives value to your app—it's made of rules that determine how your app creates, stores, and changes data.

Domain layer

The domain layer is an optional layer that sits between the UI and data layers.

The domain layer is responsible for encapsulating complex business logic, or simple business logic that is reused by multiple ViewModels. This layer is optional because not all apps will have these requirements. You should use it only when needed—for example, to handle complexity or favor reusability.

# Tools And Techniques
[Kotlin](https://kotlinlang.org/) As programming language

[Clean Architecture](https://developer.android.com/topic/architecture) Clean Architecture is a software architectural pattern that promotes the separation of concerns and the creation of modular, maintainable, and testable software systems.

[Model-View-ViewModel(MVVM)](https://developer.android.com/topic/architecture) Architecture Pattern,MVVM means a way to structure code. With MVVM, it is possible to keep the UI components of an application away from the business logic.

[Repository Pattern](https://developer.android.com/codelabs/basic-android-kotlin-training-repository-pattern#3) The repository pattern is a design pattern that isolates the data layer from the rest of the app

[Firebase](https://firebase.google.com/docs/android/setup?authuser=0&hl=en) is a set of backend cloud computing services and application development platforms provided by Google. It hosts databases, services, authentication, and integration for a variety of applications, including Android, iOS, JavaScript, Node.js, Java, Unity, PHP, and C++.

[Firebase Authentication](https://firebase.google.com/docs/auth) Firebase Authentication provides backend services, easy-to-use SDKs, and ready-made UI libraries to authenticate users to your app. It supports authentication using passwords, phone numbers, popular federated identity providers like Google, Facebook and Twitter, and more.

[Firebase FireStore](https://firebase.google.com/docs/firestore) Cloud Firestore is a flexible, scalable database for mobile, web, and server development from Firebase and Google Cloud. Like Firebase Realtime Database, it keeps your data in sync across client apps through realtime listeners and offers offline support for mobile and web so you can build responsive apps that work regardless of network latency or Internet connectivity. Cloud Firestore also offers seamless integration with other Firebase and Google Cloud products, including Cloud Functions.

[Firebase Realtime Database](https://firebase.google.com/docs/database) The Firebase Realtime Database is a cloud-hosted database. Data is stored as JSON and synchronized in realtime to every connected client. When you build cross-platform apps with our Apple platforms, Android, and JavaScript SDKs, all of your clients share one Realtime Database instance and automatically receive updates with the newest data.

[Cloud Storage for Firebase](https://firebase.google.com/docs/storage) Cloud Storage for Firebase is a powerful, simple, and cost-effective object storage service built for Google scale. The Firebase SDKs for Cloud Storage add Google security to file uploads and downloads for your Firebase apps, regardless of network quality.

[Navigation Component](https://developer.android.com/guide/navigation/navigation-getting-started) Android Jetpack's Navigation component helps you implement navigation, from simple button clicks to more complex patterns, such as app bars and the navigation drawer. 

[Retrofit](https://square.github.io/retrofit/) A type-safe HTTP client for Android and Java

[Kotlin Coroutines](https://developer.android.com/kotlin/coroutines) A coroutine is a concurrency design pattern that you can use on Android to simplify code that executes asynchronously. 

[Flows](https://developer.android.com/kotlin/flow) In coroutines, a flow is a type that can emit multiple values sequentially, as opposed to suspend functions that return only a single value. For example, you can use a flow to receive live updates from a database.

[StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) StateFlow is a state-holder observable flow that emits the current and new state updates to its collectors. The current state value can also be read through its value property. To update state and send it to the flow, assign a new value to the value property of the MutableStateFlow class.

[Fused Location Provider](https://developer.android.com/training/location/retrieve-current.html) The fused location provider is a location API in Google Play services that intelligently combines different signals to provide the location information that your app needs.

[Geocoder](https://developer.android.com/reference/android/location/Geocoder) A class for handling geocoding and reverse geocoding.

[Easy Permissions](https://github.com/vmadalin/easypermissions-ktx) EasyPermissions is a wrapper library to simplify basic system permissions logic when targeting Android M or higher.

[LiveData](https://developer.android.com/topic/libraries/architecture/livedata) LiveData is an observable data holder class. Unlike a regular observable, LiveData is lifecycle-aware, meaning it respects the lifecycle of other app components, such as activities, fragments, or services. This awareness ensures LiveData only updates app component observers that are in an active lifecycle state.

[DataStore](https://developer.android.com/topic/libraries/architecture/datastore) Jetpack DataStore is a data storage solution that allows you to store key-value pairs or typed objects with protocol buffers. DataStore uses Kotlin coroutines and Flow to store data asynchronously, consistently, and transactionally.

[Hilt](https://developer.android.com/training/dependency-injection/hilt-android)  Hilt is a dependency injection library for Android that reduces the boilerplate of doing manual dependency injection in your project. Doing manual dependency injection requires you to construct every class and its dependencies by hand, and to use containers to reuse and manage dependencies.

[Glide](https://github.com/bumptech/glide) Glide is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface. 

[SpinKit](https://github.com/ybq/Android-SpinKit) Android loading animations

[Shimmer](https://github.com/facebookarchive/shimmer-android) is an Android library that provides an easy way to add a shimmer effect to any view in your Android app.

[Pager Dots Indicator](https://github.com/tommybuonomo/dotsindicator#pager-dots-indicator)  is an Android Library that provide an easy way to add Dots Indicators for view pagers in Android

[PinView](https://github.com/ChaosLeung/PinView) A PIN view library for Android. Use to enter PIN/OTP/password etc.

[SplashScreen](https://developer.android.com/develop/ui/views/launch/splash-screen) Starting in Android 12, the SplashScreen API lets apps launch with animation, including an into-app motion at launch, a splash screen showing your app icon, and a transition to your app itself. A SplashScreen is a Window and therefore covers an Activity.

[intuit](https://github.com/intuit/sdp) An android lib that provides a new size unit - sdp (scalable dp). This size unit scales with the screen size. It can help Android developers with supporting multiple screens.
 
[Timber](https://github.com/JakeWharton/timber) This is a logger with a small, extensible API which provides utility on top of Android's normal Log class.
