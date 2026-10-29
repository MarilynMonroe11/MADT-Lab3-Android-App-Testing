# MADT Lab #3 — Moodle submission checklist

## What the assignment explicitly requires

- Unit tests for existing Android app; target **100% class/function coverage** of application logic, excluding Android, framework and generated code.
- At least **four different** UI/integration test scenarios, generated and executed.
- Separate AI sessions for unit tests, UI tests, and final GitHub commit/push.

The assignment document does **not** explicitly specify a Moodle upload filename or submission format. Verify the actual Moodle submission field before treating a suggested upload as mandatory.

## Safe hand-in package (if Moodle accepts files)

1. **Complete project ZIP** generated from the actual Android Studio project root. Include `app/src/main`, `app/src/test`, `app/src/androidTest`, Gradle settings/build files, Gradle wrapper and `README.md`. Exclude `.gradle`, `build`, `.idea`, `local.properties` and secrets.
2. **GitHub URL** to the published public repository, either in the Moodle online-text field or a `GitHub_Link.txt` file if the assignment asks for one.
3. **Test/coverage evidence** (terminal screenshot or a small PDF), *only if the Moodle submission instructions request it*. Include output for unit tests, 11/11 UI tests, both coverage tasks, and coverage percentages from the actual reports; don't fabricate numerical coverage.

Do not upload only an APK: it does not include the test sources the professor needs to inspect.
