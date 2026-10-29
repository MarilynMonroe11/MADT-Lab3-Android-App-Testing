# MADT Lab #3 — Test execution evidence (student-reported)

Date: **2026-10-29**. Environment: **Windows 11**, Gradle **8.11.1**, Gradle Daemon JDK **21**, **Pixel 6 API 35** (Android 15) for instrumentation tests.

## Commands and observed outcomes

| Gradle task | Reported console result |
| --- | --- |
| `:app:testDebugUnitTest` | `BUILD SUCCESSFUL`, 22 actionable tasks |
| `:app:verifyLogicCoverage` | `BUILD SUCCESSFUL`, 23 actionable tasks |
| `:app:createDebugAndroidTestCoverageReport` | `11/11` executed, `0 skipped`, `0 failed`; `BUILD SUCCESSFUL` |
| `:app:verifyAppCoverage` | `BUILD SUCCESSFUL`, 69 actionable tasks |

The student also confirmed these compiled classes existed in the debug build:

- `MainActivity.class`
- `MainActivity$HistoryEntry.class`
- `TextMetrics.class`
- `TextMetricsCalculator.class`

Gradle generates local reports at:

- Unit: `app/build/reports/coverage/test/debug/index.html`
- UI/instrumentation: `app/build/reports/coverage/androidTest/debug/connected/index.html`

**Evidence limits:** This document summarizes console output supplied by the student. It does not include the original HTML reports, does not independently rerun the emulator, and must not be presented as a screenshot or measured line/branch coverage report. The JaCoCo gates check class and method coverage of the configured handwritten Java classes.
