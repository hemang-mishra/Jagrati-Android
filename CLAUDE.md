# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Setup

Two files must exist before the project builds, neither of which is committed:
- `local.properties` (copy from `local.properties.template`): needs `sdk.dir`, `BASE_URL` (staging; `PROD_BASE_URL` for prod), ImageKit endpoint/public key, Google Sign-In `WEB_CLIENT_ID`, and optionally release-signing keystore properties. Some values (ImageKit keys, `WEB_CLIENT_ID`, prod config) must be obtained from maintainers.
- `google-services.json` in `app/` (Firebase/Google Sign-In), also obtained from maintainers.

`app/build.gradle.kts` reads `local.properties` via a `propOrEnv()` helper and injects values into `BuildConfig`/resValues.

## Commands

- Build debug: `./gradlew assembleDebug`
- Build release: `./gradlew assembleRelease`
- Unit tests: `./gradlew test` (JVM tests in `app/src/test/java/com/hexagraph/jagrati_android/`)
- Instrumented tests: `./gradlew connectedAndroidTest` (Espresso + Compose UI test JUnit4, needs a device/emulator)
- Lint: `./gradlew lint` (standard Android Lint; no detekt/ktlint configured)

To run a single unit test class: `./gradlew test --tests "com.hexagraph.jagrati_android.SomeTestClass"`

## Architecture

Single module (`:app`), MVVM + Repository pattern, DI via Koin. Package root: `app/src/main/java/com/hexagraph/jagrati_android/`

- `api/` — Ktor API definitions
- `model/` — data models, sub-packaged by domain (`attendance/`, `auth/`, `dao/`, `databases/` [Room], `group/`, `permission/`, `role/`, `student/`, `user/`, `village/`, `volunteer/`)
- `repository/` — repository implementations, sub-packaged by feature (`auth/`, `omniscan/`, `permission/`, `role/`, `sync/`, `user/`, `volunteer/`)
- `service/` — service layer per feature (`auth/`, `face_recognition/`, `image_service/`, `permission/`, `role/`, `user/`, `volunteer/`)
- `usecases/` — business logic (incl. `sync/`)
- `ui/` — `screens/<feature>/`, `viewmodels/`, `components/`, `navigation/`, `theme/`
- `di/` — Koin modules: `NetworkingModule`, `ServiceModule`, `RepositoryModule`, `UseCaseModule`, `ViewModelModule`, `DatabaseModule`, `PreferencesModule`
- `permissions/`, `preferences/` (DataStore), `notifications/`, `worker/` (WorkManager), `util/`, `application/`

Data flow follows UI → ViewModel → UseCase → Repository → Service/API/DAO, wired together through Koin modules in `di/`.

Notable stack choices: Ktor client for networking (auth/okhttp engine), Room for local persistence, Navigation Compose alongside the newer Navigation3 API (`navigation3.ui`/`navigation3.runtime`), Coil for images, CameraX + TensorFlow Lite (`litert`) + ML Kit + FaceNet for face-recognition-based attendance, WorkManager (with Koin integration) for background sync, Chucker for debug network inspection, Firebase Crashlytics/Messaging.

## UI conventions (from docs/design/DesignGuidelines.md)

- Brand palette follows a 60-30-10 rule: primary orange `#F17E01`, secondary purple `#AA9FF8`, accent yellow/turquoise.
- Always wrap Compose previews in `JagratiAndroidTheme {}`, with both a light preview and a dark preview (`UI_MODE_NIGHT_YES`).
- Use only `painterResource` for icons.
- Avoid code comments.
- UI must adapt to tablet/landscape layouts, not just phone portrait.

`docs/CodingGuidelines.md` exists but is currently empty.

## Related repo

The backend for this app lives in a sibling repo, `../Jagrati-backend` (Spring Boot/Kotlin). `BASE_URL` in `local.properties` points the app at it.
