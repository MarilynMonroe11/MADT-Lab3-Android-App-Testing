# Windows: how to run MADT Lab #3 tests

This archive contains Gradle Wrapper scripts and `gradle-wrapper.jar`.

1. Extract the **inside folder** `MADT_Lab3_WordCounter` directly to:
   `C:\Users\Yaroslav\AndroidStudioProjects\MADT_Lab3_WordCounter`
   (or another folder with no `&` symbol in any parent folder name).
2. Open that `MADT_Lab3_WordCounter` folder in Android Studio; complete Gradle Sync.
3. Start Pixel 6 API 35 emulator before instrumentation tests.
4. Open Android Studio Terminal (Alt+F12) and run exactly ONE command at a time:

   ` .\gradlew.bat --version `
   ` .\gradlew.bat :app:testDebugUnitTest `
   ` .\gradlew.bat :app:verifyLogicCoverage `
   ` .\gradlew.bat :app:createDebugAndroidTestCoverageReport `
   ` .\gradlew.bat :app:verifyAppCoverage `

   Remove the spaces around commands when pasting into the terminal.

Version notes: Android Gradle Plugin 8.9.2 uses Gradle 8.11.1; this project pins the Gradle daemon to JDK 21. Install JDK 21 first.

The student reported successful execution of all four verification commands on 2026-10-29. Re-run to verify on another machine; the task gates check 100% class and method coverage on configured classes, not line/branch coverage.
