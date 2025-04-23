# 🚀 CareerLinkApp
### Your Smart Android Job Finder App

CareerLinkApp is your all-in-one job search companion! 🚀 Whether you're hunting for your first job or your next big opportunity, this modern Android app makes it seamless to discover, save, and apply to jobs with an elegant UI and robust features like offline support, push notifications, and more!

---

## 📂 Table of Contents
- [🔍 Features Overview](#-features-overview)
  - [🔐 Authentication](#-authentication)
  - [💼 Job Listings](#-job-listings)
  - [📄 Job Applications](#-job-applications)
  - [🌟 UX Enhancements](#-ux-enhancements)
- [🎥 Demo Video](#-demo-video)
- [🏗️ Architecture](#-architecture)
- [⚙️ Tech Stack](#-tech-stack)
- [✨ Installation Guide](#-installation-guide)
- [📱 How to Use](#-how-to-use)

---

## 🔍 Features Overview

### 🔐 Authentication
- **Secure Login & Sign-Up** via [Firebase Authentication](https://firebase.google.com/products/auth).
- **Google Sign-In** support.
- **User Profiles** stored in [Firebase Firestore](https://firebase.google.com/products/firestore).

### 💼 Job Listings
- **Live Job Feed** from [Remotive Jobs API](https://remotive.com/api-documentation).
- **Offline Access** using [Room Database](https://developer.android.com/training/data-storage/room).
- **Favorites** support for saved jobs.

### 📄 Job Applications
- **One-tap Apply** system.
- **Push Notifications** using [Firebase Cloud Messaging](https://firebase.google.com/products/cloud-messaging).

### 🌟 UX Enhancements
- **Splash Screen & Onboarding Flow**.
- **Dark/Light Mode** with [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore).
- **Multilingual Support** (English 🇬🇧 / Arabic 🇦🇪).

---

## 🎥 Demo Video

[![CareerLinkApp Demo](https://img.youtube.com/vi/your-video-id/0.jpg)](https://www.youtube.com/watch?v=your-video-id)

---

## 🏗️ Architecture

CareerLinkApp is architected with:
- **Clean Architecture** (Presentation, Domain, Data Layers)
- **MVVM Pattern**
- **Repository Pattern**

### Clean Architecture Diagram
![Clean Architecture](https://raw.githubusercontent.com/ahmedkhalifa14/CareerLinkApp/main/assets/clean_arch.png)

### Multi-Module Design
- **App Module**: Jetpack Compose UI + ViewModels
- **Domain Module**: Business Logic + UseCases
- **Data Module**: API, Firebase, Room, DataStore Repositories

---

## ⚙️ Tech Stack
| Tool | Description |
|------|-------------|
| [Kotlin](https://kotlinlang.org/) | Main language for development |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | Declarative UI toolkit |
| [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) | Dependency Injection |
| [Room](https://developer.android.com/training/data-storage/room) | Local database |
| [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) | Key-Value preferences storage |
| [Retrofit](https://square.github.io/retrofit/) | API client |
| [OkHttp](https://square.github.io/okhttp/) | Networking layer |
| [Firebase Auth](https://firebase.google.com/products/auth) | User authentication |
| [Firebase Firestore](https://firebase.google.com/products/firestore) | User profile cloud database |
| [FCM](https://firebase.google.com/products/cloud-messaging) | Notifications |
| [Coil](https://coil-kt.github.io/coil/) | Image loading |
| [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) | Async tasks |
| [Flow](https://developer.android.com/kotlin/flow) / [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) | Reactive state updates |
| [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) | Navigation framework |
| [ConstraintLayout Compose](https://developer.android.com/jetpack/compose/layouts/constraintlayout) | UI layout tool |

---

## ✨ Installation Guide

### Prerequisites
- Android Studio Giraffe or newer
- Android SDK 33+
- Firebase Project with Auth + Firestore enabled

### Steps
```bash
git clone https://github.com/ahmedkhalifa14/CareerLinkApp.git
```
- Open the project in Android Studio
- Add `google-services.json` to `app/`
- Sync Gradle + Resolve dependencies
- Run the app on an emulator or real device

---

## 📱 How to Use

1. **Launch**: Splash screen + onboarding
2. **Sign In**: Email or Google
3. **Profile Setup**: Name, photo, and address
4. **Browse**: Search and explore job listings
5. **Save or Apply**: One-click favorite/apply
6. **Settings**: Switch theme and language

---

Find your dream job, anytime, anywhere — with **CareerLinkApp**! 🚀

