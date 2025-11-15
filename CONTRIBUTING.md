# Contributing to Jagrati Android App

Thank you for your interest in contributing to the Jagrati initiative! This document provides guidelines and setup instructions for contributors.

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- Kotlin 1.8+
- Android SDK 26 (minimum) / SDK 36 (target)
- Git for version control

### Setting Up the Project

1. **Fork and Clone the Repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/Jagrati-Android.git
   cd Jagrati-Android
   ```

2. **Configure Local Properties**
   
   Copy the template file and configure it:
   ```bash
   cp local.properties.template local.properties
   ```

   Then edit `local.properties` with your values:

   ```properties
   sdk.dir=/path/to/your/Android/Sdk
   
   # Backend Configuration
   BASE_URL=your_backend_base_url
   
   # ImageKit Configuration
   IMAGE_KIT_URL_ENDPOINT=your_imagekit_url_endpoint
   IMAGE_KIT_PUBLIC_KEY=your_imagekit_public_key
   
   # Google Sign-In
   WEB_CLIENT_ID=your_google_web_client_id
   ```


3. **Add Google Services Configuration**
   
   You need to add `google-services.json` for Firebase integration:
   
   - Place the `google-services.json` file in the `app/` directory
   - This file is required for Google Sign-In and Firebase services
   - **Important**: Request this file from the project maintainers
   - Never commit this file to version control

4. **Sync and Build**
   
   - Open the project in Android Studio
   - Let Gradle sync complete
   - Build the project to ensure everything is set up correctly

### Development Workflow

1. **Create a Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Your Changes**
   - Write clean, readable code
   - Follow the coding standards below
   - Test your changes thoroughly

3. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "Brief description of changes"
   ```

4. **Push and Create Pull Request**
   ```bash
   git push origin feature/your-feature-name
   ```
   Then create a Pull Request on GitHub.

## Coding Standards

### Kotlin Conventions

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Prefer immutability (`val` over `var`)
- Use data classes for data models

### Architecture

- Follow MVVM architecture pattern
- Use Repository pattern for data management
- ViewModels should not hold Android context
- Keep composables small and focused

### Jetpack Compose Guidelines

- Extract reusable components
- Use preview annotations for UI components
- Avoid side effects in composables
- Use `remember` and `derivedStateOf` appropriately

### Code Quality

- Add comments for complex logic
- Handle errors gracefully
- Avoid hardcoded strings (use string resources)
- Write unit tests for business logic
- Ensure no lint warnings or errors

### Git Commit Messages

- Use clear, descriptive commit messages
- Start with a verb (Add, Fix, Update, Remove)
- Keep the first line under 50 characters
- Add detailed description if needed

Example:
```
Add student attendance feature

- Implement facial recognition integration
- Add attendance marking UI
- Update student repository with attendance methods
```

## Pull Request Process

1. **Before Submitting**
   - Ensure your code builds without errors
   - Run lint checks and fix any issues
   - Test on at least one physical device
   - Update documentation if needed

2. **PR Description**
   - Describe what changes you made and why
   - Reference related issues (e.g., "Fixes #123")
   - Add screenshots for UI changes
   - List any breaking changes

3. **Review Process**
   - Be patient while maintainers review your PR
   - Respond to feedback constructively
   - Make requested changes promptly
   - Keep the PR scope focused

4. **After Approval**
   - Maintainers will merge your PR
   - Your contribution will be acknowledged

## Reporting Issues

When reporting bugs or requesting features:

- **Check existing issues** first to avoid duplicates
- **Use issue templates** if available
- **Provide details**:
  - Device model and Android version
  - Steps to reproduce
  - Expected vs actual behavior
  - Screenshots or screen recordings
  - Relevant logs or error messages

## Code Review Guidelines

As a reviewer:

- Be respectful and constructive
- Focus on code quality and maintainability
- Suggest improvements, don't just criticize
- Approve when changes meet standards

As a contributor:

- Don't take feedback personally
- Ask questions if feedback is unclear
- Make requested changes or discuss alternatives
- Thank reviewers for their time

## Project Structure

```
app/src/main/java/com/jagrati/
├── data/          # Data layer (repositories, API clients)
├── domain/        # Business logic (use cases, models)
├── ui/            # Presentation layer (composables, viewmodels)
├── di/            # Dependency injection modules
└── utils/         # Utility classes and extensions
```

## Questions?

If you have questions or need help:

- Open a discussion on GitHub
- Reach out to maintainers
- Check the backend repository for API documentation


Thank you for contributing to make education accessible to underprivileged children! 🙏

