# 'Seezu' Mobile Client (course work 2025)

**Mobile client** for the **'Seezu'** service.

[🇺🇸 English](README.md) | [🇺🇦 Українська](docs/README.uk.md)

<div style="display: flex; gap: 10px;">
  <img src="docs_resources/screenshot 1.png" width="200" />
  <img src="docs_resources/screenshot 2.png" width="200" />
  <img src="docs_resources/screenshot 3.png" width="200" />
  <img src="docs_resources/screenshot 4.png" width="200" />
</div>

---

## About the App

This self-learning app allows users to create and join courses, each of which provides a structured way to learn any topic in any field.

By progressing from topic to topic, users add their own notes and concepts (definitions / terms / ideas / postulates, etc.) for each course topic, which can later be studied in a flashcard format.

## App Features

* **Modern Interface:** Built with **Jetpack Compose** for a fast, reactive, and declarative UI.
* **Kotlin Language:** Taking advantage of a modern, safe, and concise programming language.
* **Network Requests:** Handling API calls with **Retrofit** and **Kotlin Coroutines** for asynchronous operations.

---

## Technology Stack

### Main Technologies

* **Language:** **Kotlin**
* **UI Toolkit:** **Jetpack Compose**
* **Architecture:** **MVVM** (Model-View-ViewModel)
* **Dependency Management:** **Gradle**

### Libraries and Frameworks

| Category | Library | Description |
| :--- | :--- | :--- |
| **Network** | **Retrofit** | Type-safe HTTP client for Android and Java. |
| **Serialization** | **Moshi** / **Gson** | Library to convert JSON into Kotlin/Java objects. |
| **Android Jetpack** | **ViewModel** | Maintains UI state and logic across lifecycle. |
| **Android Jetpack** | **Navigation-Compose** | Navigation between `Composable` screens using Jetpack Navigation. |
| **Asynchronous** | **Kotlin Coroutines** | Simplified asynchronous programming. |
| **Images** | **Glide** | Efficient image loading, display, and caching. |

---

## Installation and Running

### Prerequisites

* **Android Studio** (latest recommended version)
* **JDK 17**
* **Android SDK** (Min SDK: 24, Target SDK: 36)

### Instructions

#### Step 1: Install Backend Dependency

This client project depends on the shared module (`cw2025_backend_common`) from the backend repository. Before running, install this module into the local Maven repository.

Run the following commands in a *separate* directory:

```bash
# Clone backend repository
git clone https://github.com/AndriyKramar2288/cw2025_backend.git
cd cw2025_backend

# Build and install the shared module to local Maven (~/.m2)
./mvnw clean install -DskipTests
```

#### Step 2: Clone and Run the Client

1. **Clone the repository:**
```bash
# Clone the client repository
git clone https://github.com/AndriyKramar2288/cw2025_client.git
cd cw2025_client
```

2. **Open in Android Studio:**
* Open Android Studio.
* Choose **File -> Open...** and select the cloned project directory (`cw2025_client`).

3. **Sync Gradle:**
* Android Studio should prompt to sync the project automatically.
* If not, click the **Sync Project with Gradle Files** icon.

4. **Run the app:**
* Select an emulator or connected device.
* Click the green **Run** button (▶) or press **Shift + F10**.