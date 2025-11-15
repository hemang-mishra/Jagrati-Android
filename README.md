<img width="114" height="115" alt="JagratiAppLogo" src="https://github.com/user-attachments/assets/a472b331-ce73-4446-9f0a-ebfea0bfb012" />

# Jagrati Android App

## About

Jagrati is a student-run initiative at IIITDM Jabalpur providing free education to underprivileged children in nearby villages. Volunteers dedicate one hour per week to teach, and the initiative has been running for over a decade.

This app helps volunteers manage attendance, track student progress, coordinate schedules, and handle administrative tasks efficiently.

## Download the App

Get the latest version from the Google Play Store:

<a href="https://play.google.com/store/apps/details?id=com.hexagraph.jagrati_android">
  <img alt="Get it on Google Play" 
       src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" 
       width="250"/>
</a>

## Key Features

- **User Management**: Role-based access control, volunteer registration, and approval workflows
- **Student Profiles**: Comprehensive profiles with village/group organization and facial recognition data
- **Smart Attendance**: Face recognition using TensorFlow Lite and FaceNet for automated attendance

## Planned features
- **Syllabus Management**: Class-wise curriculum, topics, and resource sharing
- **Scheduling**: Volunteer schedule management with conflict resolution
- **Classwork & Homework**: Assignment tracking, submissions, and progress scoring
- **Progress Tracking**: Student proficiency monitoring and volunteer remarks
- **Volunteer Ranking**: Point-based system with leaderboards
- **Community Posts**: Share achievements, events, and updates

## Tech Stack

- **Kotlin** with **Jetpack Compose** for modern UI
- **Koin** for dependency injection
- **Ktor** for HTTP networking
- **Navigation Component 3** for app navigation
- **TensorFlow Lite** + **FaceNet** for facial recognition
- **CameraX** for camera functionality
- **Spring Boot** backend ([Backend Repository](https://github.com/hemang-mishra/Jagrati-backend/tree/master))

## Screenshots

<div style="display: flex; flex-wrap: wrap; gap: 10px;">
<img width="200" height="500" alt="Login Screen" src="https://github.com/user-attachments/assets/547559be-6a4e-49fb-bb25-10bd42c41de2" />
<img width="200" height="500" alt="Dashboard" src="https://github.com/user-attachments/assets/598cc082-65a8-4134-9a22-369db9789969" />
<img width="200" height="500" alt="Student Profile" src="https://github.com/user-attachments/assets/b0ab89bf-531a-4a1a-9ef9-026c63c67e88" />
<img width="200" height="500" alt="Attendance Marking" src="https://github.com/user-attachments/assets/256df1b7-360a-4a63-beec-a28628859856" />
<img width="200" height="500" alt="Volunteer profile" src="https://github.com/user-attachments/assets/022736a3-d14a-498c-b1c1-0d172ff4e48f" />
</div>

## Architecture

The app follows MVVM architecture with:
- Repository pattern for data management
- Dependency injection with Koin
- Kotlin Coroutines and Flow for reactive programming
- Type-safe navigation with Navigation Compose
- RESTful API integration with Ktor

## Getting Started

See [CONTRIBUTING.md](CONTRIBUTING.md) for setup instructions and development guidelines.

## License

This project is developed for educational and social welfare purposes as part of the Jagrati initiative at IIITDM Jabalpur.

## Acknowledgments

- IIITDM Jabalpur for supporting the Jagrati initiative
- Ovee Chachad for logo design and branding
- All volunteers who have contributed to this cause

---

**Developed by [Hemang Mishra](https://github.com/hemang-mishra)**

Backend: [Jagrati Backend Repository](https://github.com/hemang-mishra/Jagrati-backend/tree/master)

