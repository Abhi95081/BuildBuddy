# 🚀 BuildBuddy

BuildBuddy is an Android + server system that lets you paste a GitHub Android repo URL and automatically get a downloadable APK.  
It works like a lightweight self-hosted CI/CD pipeline (similar to App Center).

---

## 📱 Features
- Paste any public Android GitHub repo URL  
- ForgeServer clones the repo & builds it with Gradle in Docker  
- APK is generated and served via HTTP download link  
- Android app client shows build status and provides download/install button  

---

## 🛠️ Project Structure
buildbuddy/
- android-app/ # Jetpack Compose Android client
-  forge-server/ # Node.js + Docker backend (APK builder)

---

## ⚙️ Requirements
- [Android Studio](https://developer.android.com/studio) (for the Android app)
- [Node.js](https://nodejs.org/) (for ForgeServer)
- [Docker](https://docs.docker.com/get-docker/) (to run Gradle builds inside a container)
- Git installed on the server

---

## 🔧 Setup Guide

### 1️⃣ Clone this repo

git clone https://github.com/your-username/buildbuddy.git
cd buildbuddy

---
### 2️⃣ Setup ForgeServer
- cd forge-server
- npm install
  
### Create a .env file:

PORT=5000
PUBLIC_URL=http://localhost:5000
Start server:

npm start

### 3️⃣ Setup Docker

- Install Docker (see Docker Install Guide)
-Build Docker image:
-docker build -t forge-gradle .

### 4️⃣ Android App

- Open android-app/ in Android Studio.
- Update API base URL in BuildViewModel.kt to point to your ForgeServer:
- private const val BASE_URL = "http://<your-server-ip>:5000"
- Run on emulator or device.

### 📦 How ForgeServer Works

- Accepts repo URL from Android app
- Clones repo:git clone https://github.com/Abhi95081/BuildBuddy.git
- Builds with Gradle inside Docker:
./gradlew assembleDebug

- Moves generated APK to public/downloads/
Returns link like:
- http://your-server.com/downloads/app-debug.apk

### 📲 Android Workflow

- Open BuildBuddy app
- Enter repo URL
- Tap Start Build
- Wait for status → "Build complete"
- Download & install APK on device

### ✅ Roadmap

 - Support for private repos (GitHub tokens)
 - Release builds (assembleRelease)
 - CI/CD integration
 - Analytics for build history

