# Student Information System

A native Android demo in Java with XML layouts. The student portal includes demo sign-in, a dashboard, student and academic profiles, a searchable directory, and a form for adding or editing student information.

## Run in Android Studio

1. Choose **File → Open** and select this project directory.
2. Let Android Studio sync the Gradle wrapper. Install Android SDK Platform 37 if prompted. The wrapper is configured to use a Java 25 Gradle daemon.
3. In **Device Manager**, start an Android emulator with API 24 or later.
4. Choose the **app** run configuration and select **Run**.
5. On the login screen, select **Sign In** to open Adrian Reyes Santos’s sample dashboard. Both fields are optional.

If the emulator stays offline during startup, choose **Cold Boot Now** for that device in Device Manager.

On Windows, the Gradle wrapper can build the APK and run the local tests.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

The dashboard’s account card includes a sign-out action. Profile edits and added directory records use in-memory demo data for the current session. **Remember Me** stores only the entered username on this device. Passwords are discarded.

## Project layout

- `app/src/main/java/` contains the activity, form controller, and fictional student data.
- `app/src/main/res/layout/` contains the screen and reusable row layouts.
- `app/src/main/res/values/` and `app/src/main/res/drawable/` contain the shared styles, glass-style surfaces, and line icons.
