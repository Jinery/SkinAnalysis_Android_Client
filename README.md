# SkinAnalysis Android Client 📱🔬

A robust Android application designed for real-time skin condition analysis. The app leverages the device's camera to capture high-quality images, which are then processed by a remote AI-powered server to detect various skin pathologies.

---

## 🌟 Project Overview

**SkinAnalysis** is a specialized tool that bridges the gap between mobile technology and dermatological analysis. 
- **Real-time Capture**: Integrated CameraX for seamless image acquisition with low-light detection.
- **Smart Analysis**: Asynchronous image uploading and polling for AI-driven pathology detection.
- **Reliable Communication**: Managed network sessions and error handling to ensure data integrity.

---

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/) (100%)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) for a modern, declarative UI.
- **Camera**: [CameraX](https://developer.android.com/training/camerax) for stable and consistent camera performance across devices.
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/) for clean and scalable architecture.
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) for API communication.
- **Concurrency**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow/Channels](https://kotlinlang.org/docs/flow.html) for reactive data streams.
- **Local Storage**: [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for persistent user preferences and connection IDs.
- **Testing**: [MockK](https://mockk.io/), [JUnit 4](https://junit.org/junit4/), and [Kotlinx Coroutines Test](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test).

---

## 🏗 Architecture & Engineering Decisions

The project follows **Clean Architecture** principles combined with the **MVVM** (Model-View-ViewModel) pattern to ensure maintainability and testability.

### Key Decisions:
- **Reactive UI**: The UI layer observes `StateFlow` from ViewModels, ensuring a single source of truth and predictable UI updates.
- **Navigation via Channels**: Navigation events are decoupled from the UI using `Channels`, allowing the ViewModel to trigger navigation in a lifecycle-aware manner.
- **Camera Management**: Logic for CameraX is encapsulated in a `CameraManager`, which is then exposed via a `CameraRepository`. This abstracts the hardware complexity from the business logic.
- **Luminosity Analysis**: A custom `ImageAnalysis.Analyzer` is implemented to provide real-time feedback on lighting conditions, preventing low-quality captures.
- **Hilt Dependency Injection**: Used to manage component lifecycles and facilitate unit testing through mock injection.

---

## 🚀 Installation & Run

### Prerequisites
- **Android Studio** (Latest stable version recommended)
- **JDK 17**
- **Android Device/Emulator** (API level 24+)

### Steps to Run
1. **Clone the repository**:
   ```bash
   git clone https://github.com/Jinery/SkinAnalysis_Android_Client.git
   ```
2. **Open in Android Studio**:
   Import the project and wait for Gradle synchronization to finish.
3. **Configure the Server**:
   Update the `BASE_URL` in `app/src/main/java/com/kychnoo/skinanalysis_android_client/data/remote/ApiService.kt` to point to your backend server.
4. **Build and Install**:
   Click the **Run** button in Android Studio or use the command line:
   ```bash
   ./gradlew installDebug
   ```

---

## 🧪 Testing

The project includes unit tests for core components. To run the tests, use:
```bash
./gradlew test
```
Tests cover:
- **ViewModel Logic**: State transitions, error handling, and polling mechanisms.
- **Repository Operations**: Data mapping, API call handling, and Hilt integration.

---

## 🔄 CI-CD

Currently, the project is ready for integration with **GitHub Actions**. The build system is optimized for automated testing and APK generation on every push to the `master` and `dev` branches.

---

<div align="center">
  Developed with focus on Performance, Clean Code, and Reliability.
</div>
