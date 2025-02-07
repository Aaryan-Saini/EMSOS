
# 🚑 **Emergency Response App**  
*GDG Solution Challenge*

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)  
[![Issues](https://img.shields.io/github/issues/yourusername/emergency-response-app)](https://github.com/yourusername/emergency-response-app/issues)  
[![Forks](https://img.shields.io/github/forks/yourusername/emergency-response-app)](https://github.com/yourusername/emergency-response-app/network)  
[![Stars](https://img.shields.io/github/stars/yourusername/emergency-response-app)](https://github.com/yourusername/emergency-response-app/stargazers)

## 📖 **Project Overview**  
Welcome to the **Emergency Response App**, designed to **save lives** in critical situations! 🚑 This app connects users in danger with nearby rescuers, authorities, and emergency services. Built using **Google technologies**, it ensures **rapid response** and **efficient emergency management**.

### Key Features:
- 🆘 **SOS Button**: Send immediate real-time alerts to rescuers and authorities.
- 📍 **Location Tracking**: Share live GPS location for accurate rescue coordination.
- 🚨 **Emergency Contact Notifications**: Notify family and friends when the user activates the SOS.
- 🏅 **Credit & Ranking System**: Reward rescuers based on how many lives they’ve saved.
- 📲 **Real-Time Alerts**: Instant notifications for nearby rescuers to act immediately.
- 🗺️ **Google Maps Integration**: Locate hospitals, police stations, and other services.

## 🚀 **Technologies Used**

### **Frontend**
- **Android Development (Java/Kotlin)**: For mobile app interface and functionality.
  
### **Backend**
- **Firebase**:
  - **Authentication**: Google Sign-In for seamless user login.
  - **Firestore**: Real-time database for user data and SOS logs.
  - **Cloud Functions**: Trigger alerts and notifications.
  - **Firebase Cloud Messaging (FCM)**: Push notifications for real-time communication.
  
- **Google Maps API**: For real-time location tracking and navigation.

---

## 🛠 **Installation**

Follow the instructions to set up the **Emergency Response App** locally.

### **Prerequisites**:
- **Android Studio**
- **Google Firebase account**
- **Google Cloud Console**

### **Steps to Set Up**:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/emergency-response-app.git
   cd emergency-response-app
   ```

2. **Set Up Firebase**:
   - Create a project on [Firebase Console](https://console.firebase.google.com/).
   - Add your Android app to the Firebase project.
   - Download `google-services.json` and place it in the `/app` directory.

3. **Configure Firebase Dependencies**:
   ```gradle
   implementation 'com.google.firebase:firebase-auth:21.0.3'
   implementation 'com.google.firebase:firebase-firestore:24.0.1'
   implementation 'com.google.firebase:firebase-messaging:23.0.0'
   ```

4. **Set Up Google Maps API**:
   - Create a project on [Google Cloud Console](https://console.cloud.google.com/).
   - Enable the **Google Maps SDK for Android** and add your API key to `AndroidManifest.xml`:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="YOUR_API_KEY" />
     ```

5. **Run the App**:
   Open the project in **Android Studio** and run it on your Android device or emulator.

---

## 💬 **How It Works**

1. **SOS Button**: The user presses the **SOS button** when in danger. An alert is sent to nearby rescuers and emergency contacts.
2. **Location Sharing**: The app uses **GPS** to share the user’s location with rescuers.
3. **Real-Time Notifications**: Push notifications are sent to rescuers and authorities via **FCM**.
4. **Ranking & Credits**: Rescuers earn points based on the number of lives saved, leading to a leaderboard of top rescuers.
5. **Emergency Contacts**: The app notifies the user's family when an SOS alert is activated.

---

## 🧑‍🤝‍🧑 **Team Members**

| Name          | Role                        |
| ------------- | --------------------------- |
| **Aaryan Saini** | Project Lead (UI/UX, Firebase) |
| **Charvi**      | Backend & Machine Learning    |
| **Anuj**        | Frontend Development (Android) |
| **Shagun**      | Backend (API Development)     |

---

## 📊 **Project Stats**

![GitHub Stats](https://github-readme-stats.vercel.app/api?username=yourusername&show_icons=true&hide_title=true&count_private=true&theme=radical)  
![Top Languages](https://github-readme-stats.vercel.app/api/top-langs/?username=yourusername&layout=compact&hide_title=true&theme=radical)

---

## 📝 **Contributing**

We welcome contributions to improve this project. Here's how you can get involved:

1. **Fork the repository** to your own GitHub account.
2. **Clone** it to your local machine:
   ```bash
   git clone https://github.com/yourusername/emergency-response-app.git
   ```
3. **Create a branch** for the feature or fix you want to contribute to:
   ```bash
   git checkout -b feature/your-feature
   ```
4. **Make your changes** and commit them:
   ```bash
   git commit -m "Description of your changes"
   ```
5. **Push** your changes to your fork:
   ```bash
   git push origin feature/your-feature
   ```
6. **Open a Pull Request** to the main repository.

---

## 💡 **Future Enhancements**

- **Machine Learning Integration**: Add **fall detection** and **voice recognition** for automatic SOS triggering.
- **AI-powered Rescue Dispatching**: Automatically dispatch nearby authorities and services based on AI algorithms.
- **Multi-Language Support**: Extend the app to support multiple languages for a global audience.

---

## 📝 **License**

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more details.

---

**Let's work together to build a safer world! 🌍🚑**

---

