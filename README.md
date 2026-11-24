# Student Information System

A native Java and XML student portal with on-device sign-in, a dashboard, student and academic profiles, and a searchable directory.

## Run in Android Studio

1. Choose **File → Open** and select this project directory.
2. Let Android Studio sync the Gradle wrapper. Install Android SDK Platform 37 if prompted. The wrapper is configured to use a Java 25 Gradle daemon.
3. In **Device Manager**, start an Android emulator with API 24 or later.
4. Choose the **app** run configuration and select **Run**.
5. Select **Use Sample Account** to open Adrian Reyes Santos’s dashboard. The initial sample credentials are `adrian.santos` and `student2026`.

If the emulator stays offline during startup, choose **Cold Boot Now** for that device in Device Manager.

On Windows, the Gradle wrapper can build the APK and run the local tests.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

Student records and profile edits are saved on the device. Assign a username and password when adding a student so they can sign in with their own record. To update your password, open **Edit Profile**. Changing the sample account’s password removes its quick sign-in shortcut.

**Remember Me** saves the username for the next sign-in. Account passwords are stored as salted hashes. Use **Account Information** on the dashboard to sign out.

## Project layout

- `app/src/main/java/` contains the activity, form controller, and fictional student data.
- `app/src/main/res/layout/` contains the screen and reusable row layouts.
- `app/src/main/res/values/` and `app/src/main/res/drawable/` contain the shared styles, glass-style surfaces, and line icons.
