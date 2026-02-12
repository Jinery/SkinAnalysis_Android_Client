# SkinAnalysis Android 📱🔬


<div align="center">
  
![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-blue.svg?style=flat&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2026.01.00-green.svg?style=flat&logo=jetpackcompose)

</div>

Android client for skin condition analysis. Users can select images from the gallery and send them to a Python-based server for processing.

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio Narwall 3 Feature Drop | 2025.1.3** or newer  
  [Download Android Studio](https://developer.android.com/studio)

### 📥 Installation
1. **Clone the repository**
```bash
git clone [https://github.com/Jinery/SkinAnalysis_Android_Client.git
```
2. **Open in Android Studio**
* Launch Android Studio
* Click File → Open
* Navigate to and select the project folder
3. **Sync the project**
* Wait for Gradle sync to complete automatically
* If it doesn't start: File → Sync Project with Gradle Files

### 🔨 Build APK / App Bundle
1. **Generate signed build**
```text
Build → Generate Signed App Bundle / APK
```
2. **Select format**
* Choose APK for direct installation
* Or Android App Bundle for Google Play Store

3. **Finish**
* Click Finish
* Locate APK at: app/build/outputs/apk/release/

### 🌐 Server Configuration
*⚠️ Important: The app needs to connect to your Python backend server*
1. **Open ApiService.kt**
```text
app/src/main/java/data/remote/ApiService.kt
```
2. **Find BASE_URL constant**
```kotlin
const val BASE_URL = "http://192.168.1.71:8000"
```
3. **Replace with your server address**
```kotlin
// Local network example:
const val BASE_URL = "http://192.168.1.100:8000"

// Public IP example:
const val BASE_URL = "http://your-public-ip:8000"

// Domain example:
const val BASE_URL = "https://api.yourdomain.com"
```

4. **Save and rebuild**

### 📱 Features
* ✅ Select images from device gallery
* ✅ Send images to server for analysis
* ✅ Display processing results
* 🎨 Clean Material 3 design
* 🌓 Dark/light theme support
* 🌍 Support for multiple languages

### 🔧 Troubleshooting
**Gradle sync fails**
* Update Android Studio to latest version
* Invalidate caches: File → Invalidate Caches and Restart

**Can't connect to server**
* Check if phone and server are on same network
* Verify server is running
* Disable firewall temporarily
* Use 10.0.2.2 for Android Emulator to access localhost

**Build errors**
* Clean project: Build → Clean Project
* Rebuild: Build → Rebuild Project

### 🤝 Contributing
**Fork the project**
1. Create feature branch: git checkout -b feature/amazing-feature
2. Commit changes: git commit -m 'Add amazing feature'
3. Push: git push origin feature/amazing-feature
4. Open a Pull Request

<div align="center"> Made with ❤️ and Kotlin </div>
