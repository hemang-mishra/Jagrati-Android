<img width="114" height="115" alt="JagratiAppLogo" src="https://github.com/user-attachments/assets/a472b331-ce73-4446-9f0a-ebfea0bfb012" />

# Jagrati Android App

## About

Jagrati is a student-run initiative at IIITDM Jabalpur providing free education to underprivileged children in nearby villages. Volunteers dedicate one hour per week to teach, and the initiative has been running for over a decade.

This app helps volunteers manage attendance, track student progress, coordinate schedules, and handle administrative tasks efficiently.

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
<img width="200" height="400" alt="Login Screen" src="https://github.com/user-attachments/assets/e1ace246-1022-4703-802d-d10d4bb3af77" />
<img width="200" height="400" alt="Dashboard" src="https://github.com/user-attachments/assets/f44cb7a9-4bea-4144-8cbf-6fb41be2d5cc" />
<img width="200" height="400" alt="Student List" src="https://github.com/user-attachments/assets/9e1907b4-068a-4103-9060-900ee1f3a664" />
<img width="200" height="400" alt="Attendance Marking" src="https://github.com/user-attachments/assets/adcfca40-2e25-410f-a0b1-b1d43ba7b0cf" />
<img width="200" height="400" alt="Profile Management" src="https://github.com/user-attachments/assets/dfe9d43f-da5a-4d8f-9d87-28ea0746e6eb" />
<img width="200" height="400" alt="Schedule View" src="https://github.com/user-attachments/assets/040398d1-a8f2-484e-a9c1-3fb64bc63cd2" />
<img width="200" height="400" alt="Progress Tracking" src="https://github.com/user-attachments/assets/7a786bee-514f-484d-9026-10872fd30747" />
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

