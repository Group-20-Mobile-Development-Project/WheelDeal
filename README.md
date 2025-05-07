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

| Landing Page                        | Home Page                            | Car Listings                  |
| ----------------------------------- | ------------------------------------ | ----------------------------- |
| ![Loading_Screen](https://github.com/user-attachments/assets/649003a5-ccb6-4a52-9f65-9443116ff478) | ![homeScreen](https://github.com/user-attachments/assets/1645ec7a-eb19-453e-9d12-28f958883b22) |![carlisitng](https://github.com/user-attachments/assets/31bd7e22-c9d6-40a8-9a95-ddc8a3b1b856) |

| Car Details                         | Owner Details                        | Chat Interface                |
| ----------------------------------- | ------------------------------------ | ----------------------------- |
| ![cardetails](https://github.com/user-attachments/assets/1d1e4124-0a04-46b4-be20-f1ec7b65c9c3) | ![ownerdetails](https://github.com/user-attachments/assets/df25c76b-4ddd-4c93-8204-6a0e59419ead) | ![ChatSystem](https://github.com/user-attachments/assets/ddeac61a-3b0c-4ffe-8632-231c8f153faf) |

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

## 🙌 Acknowledgements

We gratefully acknowledge the open-source libraries and tools that made this project possible:

| Library / Tool                          | Purpose                                   | License                 |
| --------------------------------------- | ----------------------------------------- | ----------------------- |
| **Jetpack Compose**                     | Modern UI framework for Android           | Apache 2.0              |
| **Firebase (Auth, Firestore, Storage)** | Backend services (Auth, DB, File storage) | Proprietary (Free Tier) |
| **Coil**                                | Image loading in Compose                  | Apache 2.0              |
| **Gson**                                | JSON serialization/deserialization        | Apache 2.0              |
| **Google Maps SDK**                     | Location and map integration              | Google APIs             |
| **Material3 (Compose)**                 | Material Design components                | Apache 2.0              |

All dependencies used follow either open-source or permissive licenses compatible with educational and non-commercial use.

## 📝 Purpose
This project is built for educational purposes as part of a **Mobile App Development Course** at OAMK.

---
