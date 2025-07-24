
<img width="114" height="115" alt="JagratiAppLogo" src="https://github.com/user-attachments/assets/a472b331-ce73-4446-9f0a-ebfea0bfb012" />
# Jagrati Android App

## Introduction

Jagrati is a non-profit student initiative at IIITDM Jabalpur, dedicated to educational and social welfare. It focuses on providing quality education to underprivileged children in the surrounding areas of the college. Volunteers from the college dedicate just one hour per week to teach these students, and the initiative has been running successfully for over a decade. 

This Android app is designed to streamline and organize the various processes associated with this initiative, making it easier for volunteers to manage their involvement and contribute effectively to the cause.

## Features

The Jagrati app is designed primarily for volunteers and encompasses a comprehensive set of features organized into nine main modules:

### 1. **User Management & Role System**
- Role-based access control with different permission levels
- User registration and authentication
- Volunteer request management with approval workflows
- Role transition requests (upgrade/downgrade)

### 2. **Student & Volunteer Profiles**
- Comprehensive student and volunteer profile management
- Village and group organization
- Facial recognition data storage for attendance
- Student group assignment history tracking

### 3. **Attendance Management**
- **Automated Attendance**: Uses facial recognition to mark student attendance
- **TensorFlow Lite Integration**: Efficient on-device machine learning for fast and secure processing
- **Face Recognition with FaceNet**: Ensures accurate identification of students
- **CameraX Support**: Provides seamless camera functionality
- Volunteer attendance tracking with role-based marking

### 4. **Syllabus Management**
- Subject, topic, and subtopic organization
- Class-wise curriculum management
- Resource sharing by volunteers
- Educational content management

### 5. **Volunteer Scheduling**
- Day-wise volunteer schedule management
- Group and subject assignment
- Schedule modification requests
- Automated scheduling conflicts resolution

### 6. **Classwork & Homework**
- Class session management
- Homework assignment and tracking
- Student submission monitoring
- Classwork documentation with image support
- Progress scoring and feedback system

### 7. **Student Progress & Remarks**
- Individual student proficiency tracking
- Topic-wise progress monitoring
- Volunteer remarks and observations
- Community-based remark validation system

### 8. **Volunteer Ranking & Activities**
- Point-based volunteer ranking system
- Activity logging and recognition
- Performance tracking across different engagement types
- Leaderboard and achievement system

### 9. **Posts & Social Features**
- Community posts for achievements and events
- Image sharing capabilities
- Like and engagement system
- Category-based content organization

## Tech Stack

- **Android (Jetpack Compose)** – Modern UI development with declarative UI
- **Koin** – Dependency injection framework
- **Ktor HTTP Client** – Network communication
- **Navigation Component 3** – Latest navigation architecture
- **TensorFlow Lite** – Machine learning model for facial recognition
- **FaceNet** – Facial feature extraction
- **CameraX** – Camera API for capturing student images
- **Spring Boot Backend** – RESTful API server ([Backend Repository](https://github.com/hemang-mishra/Jagrati-backend/tree/master))

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

The app follows modern Android development practices with:
- **MVVM Architecture** with Jetpack Compose
- **Repository Pattern** for data management
- **Dependency Injection** using Koin
- **Reactive Programming** with Kotlin Coroutines and Flow
- **Type-safe Navigation** with Navigation Compose
- **RESTful API Integration** with Ktor client

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Kotlin 1.8+
- Android SDK 24 (minimum) / SDK 34 (target)
- Backend server running ([Setup Instructions](https://github.com/hemang-mishra/Jagrati-backend/tree/master))

### Installation
1. Clone the repository
```bash
git clone [repository-url]
cd jagrati-android-app
```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Configure the backend API endpoint in the app configuration

5. Build and run the application

## Contributing Guidelines

We welcome contributions from the community! Please follow these guidelines:

### How to Contribute

1. **Fork the repository** and create your feature branch from `main`
2. **Create a new branch** for your feature: `git checkout -b feature/your-feature-name`
3. **Make your changes** following the coding standards below
4. **Test your changes** thoroughly
5. **Commit your changes** with descriptive commit messages
6. **Push to your fork** and create a Pull Request

### Coding Standards

- Follow **Kotlin coding conventions**
- Use **meaningful variable and function names**
- Add **comments for complex logic**
- Ensure **proper error handling**
- Write **unit tests** for new features
- Follow **MVVM architecture patterns**
- Use **Jetpack Compose best practices**

### Pull Request Process

1. Ensure your code builds without warnings
2. Update documentation if needed
3. Add screenshots for UI changes
4. Reference any related issues in your PR description
5. Request review from maintainers

### Code Review

All submissions require review. We use GitHub pull requests for this purpose. Please be patient during the review process and be open to feedback.

### Issues and Bug Reports

- Use the **GitHub Issues** tab to report bugs
- Provide **detailed reproduction steps**
- Include **device information** and **Android version**
- Add **screenshots or videos** if applicable

## License

This project is developed for educational and social welfare purposes as part of the Jagrati initiative at IIITDM Jabalpur.

## Acknowledgments

- **IIITDM Jabalpur** for supporting the Jagrati initiative
- **Jagrati volunteers** who have contributed to this cause for over a decade
- **Open source community** for the amazing tools and libraries

---

**Built with ❤️ by [Hemang Mishra](https://github.com/hemang-mishra)**

For backend API documentation and setup, visit: [Jagrati Backend Repository](https://github.com/hemang-mishra/Jagrati-backend/tree/master)
