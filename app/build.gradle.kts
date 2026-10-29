import java.math.BigDecimal
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

plugins {
    id("com.android.application")
    jacoco
}

android {
    namespace = "com.example.madtlab2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.madtlab2"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test:rules:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}

// Demonstrate 100% class AND method coverage for pure-Java application logic.
// Note: this is verified using JaCoCo from real Gradle test execution, not
// inferred from the number of tests. MainActivity is separately exercised
// with Android instrumentation and has its own coverage HTML report.
val verifyLogicCoverage by tasks.registering(JacocoCoverageVerification::class) {
    dependsOn("testDebugUnitTest")
    classDirectories.setFrom(fileTree(layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")) {
        include("com/example/madtlab2/TextMetrics.class")
        include("com/example/madtlab2/TextMetricsCalculator.class")
    })
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("**/testDebugUnitTest.exec")
    })
    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "CLASS"
                value = "COVEREDRATIO"
                minimum = BigDecimal.ONE
            }
            limit {
                counter = "METHOD"
                value = "COVEREDRATIO"
                minimum = BigDecimal.ONE
            }
        }
    }
}

// Cross-type coverage gate for *all* hand-written Java app classes. It
// combines local JVM .exec and device/instrumentation .ec coverage data.
// Run this task only while an emulator/device is connected; it runs both suites.
val verifyAppCoverage by tasks.registering(JacocoCoverageVerification::class) {
    dependsOn("createDebugUnitTestCoverageReport", "createDebugAndroidTestCoverageReport")
    classDirectories.setFrom(fileTree(layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")) {
        include("com/example/madtlab2/*.class")
    })
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("**/testDebugUnitTest.exec")
        include("**/*.ec")
    })
    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "CLASS"
                value = "COVEREDRATIO"
                minimum = BigDecimal.ONE
            }
            limit {
                counter = "METHOD"
                value = "COVEREDRATIO"
                minimum = BigDecimal.ONE
            }
        }
    }
}
