# Kito - KIIT Attendance Checker

A Kotlin-based Android application that securely fetches attendance data from the KIIT University SAP Portal. This app is designed to provide students with easy access to their attendance records without storing any sensitive information.

The app is Krazzy.

## 🚀 Features

- **Secure Login**: Enter your KIIT credentials to access attendance data
- **Real-time Fetching**: Get the latest attendance information directly from SAP Portal
- **Privacy Focused**: No credentials or personal data are stored
- **User-friendly Interface**: Clean UI showing subjects and attendance percentages

## 🔒 Security & Privacy

- **Zero Data Storage**: No usernames, passwords, or personal data are ever stored
- **Secure Wiping**: Credentials are immediately wiped from memory after use
- **Encrypted Communication**: All network requests use HTTPS with proper headers
- **Session Management**: Proper login/logout with session cleanup

## 🏗 Architecture

The application separates sensitive SAP Portal interaction logic from the public interface:

- **Public Code**: Core app structure, UI, and data models
- **Sensitive Code**: SAP Portal specific endpoints, parameters, and parsing logic (kept locally, not in repo)

## 🛠 Tech Stack

- **Language**: Kotlin
- **Networking**: OkHttp3 for HTTP requests
- **HTML Parsing**: JSoup for parsing SAP Portal responses
- **Coroutines**: For asynchronous operations
- **SAP Portal Integration**: Direct communication with KIIT's SAP infrastructure

## ⚙️ Build Setup

This project uses Gradle for dependency management and building:

```bash
# Build the project
./gradlew build

# Assemble debug APK
./gradlew assembleDebug
```

## 📁 Project Structure

```
app/
├── src/main/java/com/kito/
│   ├── MainActivity.kt          # Main UI
│   ├── SapPortalClient.kt       # Public interface (no sensitive details)
│   └── sap/
│       ├── sensitive/           # PRIVATE - SAP Portal logic (in .gitignore)
│       └── SapPortalClient.kt   # Public interface
├── src/main/res/                # Resources
└── build.gradle.kts             # Build configuration
```

## 🚫 Important Notes

- The `app/src/main/java/com/kito/sap/sensitive/` directory contains the actual SAP Portal interaction code and is excluded from the repository via `.gitignore`
- This ensures that the reverse-engineered SAP Portal details are not exposed publicly
- The application works by combining the public interface with the local sensitive code

## 🤝 Contributing

Feel free to submit issues or pull requests. All contributions are welcome!

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## ⚠️ Disclaimer

This application is a third-party integration and is not officially affiliated with KIIT University. Use at your own discretion and ensure compliance with the university's terms of service.
