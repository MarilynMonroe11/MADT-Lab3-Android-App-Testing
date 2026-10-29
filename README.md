# MADT Lab #3 — Android App Testing

Android application testing laboratory work for **Mobile Applications Development Techniques (MADT)**. The existing **Word Counter** Android application is used as the system under test. The laboratory focuses on automated unit tests, UI/instrumentation tests, and code coverage.

## Scope

- **Unit tests** check application logic at the class/function level.
- **UI/instrumentation tests** exercise the Android application on an emulator/device.
- **Coverage checks** are provided by the project's Gradle tasks `verifyLogicCoverage` and `verifyAppCoverage`.
- The underlying application logic is preserved from the working project.

The laboratory assignment requires 100% coverage at the class/function level for application logic (excluding Android/framework/generated code), and at least four distinct UI/integration test scenarios. Passing a Gradle task by itself does **not** establish a specific coverage percentage; verify the generated reports.

## Requirements

- Android Studio with the Android SDK installed.
- An Android 15 (API 35) emulator, e.g. **Pixel 6 API 35**, for UI tests.
- A compatible Gradle JDK. The most recent tested project archive was named `MADT_Lab3_WordCounter_JVM21_Fixed.zip`; configure **JDK 21** if using that version of the project.
- The project-provided Gradle wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/`).

## Open and run

1. Clone this repository and open its **root folder** in Android Studio (the directory containing `settings.gradle` or `settings.gradle.kts`).
2. Let Android Studio complete Gradle Sync and install any required Android SDK components.
3. Select an Android emulator or connected device and run the `app` configuration.
4. For instrumentation tests, start the **Pixel 6 API 35** emulator first.

## Run automated tests

In **Windows PowerShell**, from the project root:

```powershell
# Unit tests (local JVM)
.\gradlew.bat :app:testDebugUnitTest

# UI/instrumentation tests (running emulator required)
.\gradlew.bat :app:connectedDebugAndroidTest

# Project-specific coverage verification
.\gradlew.bat :app:verifyLogicCoverage
.\gradlew.bat :app:verifyAppCoverage
```

On Linux/macOS, use `./gradlew` instead of `.\gradlew.bat`.

The coverage commands above are **custom tasks defined by this project's Gradle configuration**; they are not built-in tasks available in every Android project.

Depending on the configuration, `verifyAppCoverage` can run connected Android tests and generate a report through `createDebugAndroidTestCoverageReport`. Locate generated results under `app/build/reports/` (and the applicable Android test coverage output directories). Exact report filenames/paths depend on the project configuration.

## Test results (local execution)

The following Gradle results were reported by the student from a Windows 11 environment on **2026-10-29**. The GitHub publishing environment did not independently rerun an Android emulator:

| Check | Reported result |
| --- | --- |
| Unit tests | Passed |
| UI tests | **11 / 11 passed** on Pixel 6 API 35 |
| `verifyLogicCoverage` | Passed |
| `verifyAppCoverage` | Passed |

**Validation details:** `:app:verifyLogicCoverage` and `:app:verifyAppCoverage` both finished with `BUILD SUCCESSFUL`. Their JaCoCo rules explicitly enforce a `1.00` covered ratio for class and method counters over the configured application classes. The student also checked that `MainActivity.class`, `MainActivity$HistoryEntry.class`, `TextMetrics.class` and `TextMetricsCalculator.class` were compiled. These facts support the project's class/method coverage gate but do **not** establish 100% line or branch coverage. For actual measured figures, open the generated HTML reports locally; build output is intentionally not committed.

### The 11 UI scenarios

1. Calculate four metrics for normal text.
2. Reject whitespace-only input without modifying history.
3. Generate and calculate a sample sentence.
4. Clear input while retaining history.
5. Restore source text from history.
6. Preserve metrics/history on activity recreation.
7. Verify that `9:30` counts as one token.
8. Generate a second sample and calculate it.
9. Truncate long history previews but restore full text.
10. Preserve generator progress on activity recreation.
11. Preserve safe padding beneath Android system bars.

## Source and tests

The repository must include the complete Android Studio project, especially:

- `app/src/main/` — original application code and resources;
- `app/src/test/` — JVM/unit tests;
- `app/src/androidTest/` — Android instrumentation/UI tests;
- root and app Gradle build configuration;
- `gradle/wrapper/`, `gradlew`, and `gradlew.bat` — reproducible Gradle wrapper;
- this `README.md`.

Generated `build/` output, Android Studio caches, personal `local.properties`, device-specific data, and secrets are intentionally excluded from Git.

## Coursework

**MADT Lab #3 — Android App Testing (Viber version).** The assignment specifies separate AI sessions for unit-test generation, UI-test generation, and GitHub publication. This repository corresponds to the final publication stage.
