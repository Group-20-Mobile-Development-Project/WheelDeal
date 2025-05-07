# 🚗 WheelDeal

**WheelDeal** is a modern second-hand car marketplace app that connects buyers and sellers through a clean, intuitive mobile experience. Built with the latest Android technologies, it offers secure authentication, real-time listings, chat functionality, and smart location-based features to help users find their perfect car or buyer faster.

---

## 🔥 Key Features

* **🔐 Firebase Authentication**
  Sign up/login securely using email & password

* **📋 Car Listings**
  Post detailed car listings with specifications, price, and images.

* **🛠️ Create, Edit & Delete Listings**
  Sellers can manage their listings with ease using Firestore integration.

* **📍 Location-Based Listings**
  Uses device location to recommend nearby cars.

* **💬 1-on-1 Chat System**
  Real-time Firestore messaging system between buyer and seller, with message history.

* **❤️ Favorites & Filtering**
  Mark listings as favorites and filter by brand, price, fuel type, transmission, and more.

* **🔔 Notifications (FCM Ready)**
  In-app notification support for messages and listing updates.

* **📱 Modern UI with Jetpack Compose**
  Clean and responsive layout following Material Design 3 guidelines and Figma branding.

* **🌐 Persistent Sessions & User Profiles**
  Users stay logged in, and profile information is synced from Firestore.

---

## 🧱 Tech Stack

| Layer               | Technology                                          |
| ------------------- | --------------------------------------------------- |
| **Frontend**        | Jetpack Compose, Material 3 UI                      |
| **Architecture**    | MVVM + StateFlow                                    |
| **Backend**         | Firebase Firestore, Firebase Auth, Firebase Storage |
| **Real-time**       | Firestore snapshot listeners for chat & listings    |
| **Notifications**   | Firebase Cloud Messaging (planned)                  |
| **Location**        | Fused Location Provider (Google Maps SDK planned)   |
| **Image Upload**    | Firebase Storage                                    |
| **Navigation**      | Navigation Compose                                  |
| **Design**          | Figma, Poppins & Cabin fonts                        |
| **Version Control** | GitHub + Kanban board (Agile)                       |

---

## 🧪 Setup & Installation

> 💡 Minimum requirements: Android Studio Flamingo+, Android 8+, Kotlin 1.8+

1. **Clone the Repository**

   ```bash
   git clone https://github.com/Group-20-Mobile-Development-Project/WheelDeal.git
   ```

2. **Open the Project in Android Studio**

3. **Firebase Setup**

   * Get your `google-services.json` from Firebase Console.
   * Place it in the `app/` directory.

4. **Enable Required Firebase Services**

   * Firestore Database
   * Firebase Authentication
   * Firebase Storage
   * (Optional) Firebase App Check / FCM

5. **Sync Gradle and Build the App**

   * Make sure all dependencies resolve properly.
   * Click **Run** to launch on emulator or real device.

---

## 🖼️ App Screens

* Splash → Landing → Main
* Home, Buy, Sell, Favorites, Account (via Bottom Navigation)
* Notification, Chat (Via Top Navigation)

---
## 🖼️ App Preview

| Landing Page                        | Car Listings                         |
| ----------------------------------- | ------------------------------------ | 
| ![Landing](screenshots/landing.png) | ![Listing](screenshots/listings.png) | 


| Car Details                         | Chat Interface                       |
| ----------------------------------- | ------------------------------------ | 
| ![Landing](screenshots/landing.png) | ![Listing](screenshots/listings.png) |

---

## ✍️ Contributors

| Name                  | Role                                                  |
| --------------------- | ----------------------------------------------------- |
| **Sauhardha Khatri**  | Team Lead, Backend & Frontend Integration, Listings, Chat System, UI/UX Design|
| **Sanjaya Bhattarai** | UI/UX Design, Car Details, GPS, Ads Integration|
| **Nishan Thapa**      | UI/UX Design, Notification, Favorites|

---

## 🎨 Design Prototype

View our design flow and components on Figma:

🔗 [WheelDeal UI/UX Prototype (Figma)](https://www.figma.com/proto/Bz43whHLqtkwpfWbqLffUr/Wheel-Deal-UI%2FUX?node-id=0-1&t=6r6wIdqmDql9iLhA-1)

---

## 🚀 Logo

![WheelDeal Logo](https://github.com/user-attachments/assets/1fa26823-9425-41a0-b62b-bac07eca6442)

---

## 📝 License
This project is built for educational purposes as part of a **Mobile App Development Course** at OAMK.

---
