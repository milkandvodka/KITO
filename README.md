# Kito – KIIT Attendance Checker

Kito is a Kotlin-based Android application that helps KIIT students quickly view their attendance information in a simple, distraction-free interface.

The app is built with a strong focus on **privacy, transparency, and user trust**, ensuring that users always know what the app does — and just as importantly, what it does **not** do. 

The app has no backend, no external servers to communicate with. It is just a frontend which sends requests and receives response from SAP, that's it.

📲 **Available on Google Play**  
👉 https://play.google.com/store/apps/details?id=com.kito

---


## 🚀 Features

### Secure Sign-In
Access your attendance using your own credentials, entered only when needed.

### Live Attendance Data
Fetches up-to-date attendance details whenever you check.

### Privacy-First Design
No credentials, personal data, or attendance records are stored.

### Clean & Simple UI
Easily view subjects and attendance percentages at a glance.

---

## 🔒 Privacy & Transparency

Kito is designed around a single principle: **your data stays with you**.

- **No Data Storage**  
  User credentials and personal information are never saved locally or remotely.

- **Ephemeral Usage**  
  Credentials exist only in memory for the duration of the request and are immediately cleared afterward.

- **Secure Communication**  
  All network communication uses encrypted connections.

- **No Analytics / Tracking**  
  No trackers, ads, or hidden data collection of any kind.

If an app ever asks for your credentials, you deserve to know **exactly why** and **how they’re handled**.  
Do **not** share your credentials with software or individuals who are not upfront about their behavior and intentions.

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **Networking**: OkHttp
- **HTML Parsing**: JSoup
- **Asynchronous Operations**: Kotlin Coroutines
- **Platform**: Android

---

## ⚙️ Build Instructions

This project uses Gradle for building:

```bash
# Build the project
./gradlew build

# Assemble debug APK
./gradlew assembleDebug

```


## 🤝 Contributing

Contributions are welcome.

If you have ideas, improvements, or bug reports, feel free to open an issue or submit a pull request.
Please ensure that any contributions respect user privacy and maintain the transparency principles of the project.

---

## 📄 License

See the `LICENSE` file for more details.

---

## ⚠️ User Notice

This project is independently developed for educational and personal-use purposes.

Your credentials are **used only to fetch your attendance and are never stored, logged, or shared**.
Kito is built with transparency at its core — the app clearly communicates what it does, how your data is handled, and what it does **not** do.

That said, always be cautious with your credentials online.
Only trust applications that are open and explicit about their behavior, data usage, and limitations — **transparency is non-negotiable**.
