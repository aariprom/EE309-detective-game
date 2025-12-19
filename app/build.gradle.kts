import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    // 커버리지는 내장 옵션 대신 순정 jacoco 사용
    id("jacoco")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

fun loadEnv(name: String): String? =
    localProperties.getProperty(name) ?: System.getenv(name)

jacoco {
    toolVersion = "0.8.12"
}

kotlin {
    compilerOptions {
        jvmTarget.set(
            org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        )
    }
}

android {
    namespace = "com.ee309.detectivegame"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ee309.detectivegame"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "1.2"

        // testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // override above by hilt test runner
        testInstrumentationRunner = "com.ee309.detectivegame.HiltTestRunner"
        vectorDrawables { useSupportLibrary = true }

        val upstageApiKey = loadEnv("UPSTAGE_API_KEY") ?: ""
        val upstageBaseUrl = loadEnv("UPSTAGE_BASE_URL") ?: ""

        buildConfigField("String", "UPSTAGE_API_KEY", localProperties.getProperty("UPSTAGE_API_KEY", "\"${upstageApiKey}\""))
        buildConfigField("String", "UPSTAGE_BASE_URL", localProperties.getProperty("UPSTAGE_BASE_URL", "\"${upstageBaseUrl}\""))
    }

    // Signing configuration for release builds (used by GitHub Actions)
    signingConfigs {
        create("release") {
            val keystoreFilePath = loadEnv("KEYSTORE_FILE")
            if (!keystoreFilePath.isNullOrEmpty()) {
                val keystoreFile = rootProject.file(keystoreFilePath)
                if (keystoreFile.exists()) {
                    storeFile = keystoreFile
                    storePassword = loadEnv("KEYSTORE_PASSWORD") ?: ""
                    keyAlias = loadEnv("KEY_ALIAS") ?: ""
                    keyPassword = loadEnv("KEY_PASSWORD") ?: ""
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Apply release signing config if keystore is available
            val releaseSigningConfig = signingConfigs.findByName("release")
            if (releaseSigningConfig?.storeFile != null) {
                signingConfig = releaseSigningConfig
            }
        }
        debug {
            // 🔴 문제였던 내장 유닛 테스트 커버리지는 비활성화
            // enableUnitTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { 
        compose = true 
        buildConfig = true
    }
    
    // marked unstable to use with @Incubating, suppress for now
    @Suppress("UnstableApiUsage")
    composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.activity:activity-compose:1.12.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2025.11.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.9.6")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.10.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.57.2")
    implementation("androidx.test:runner:1.7.0")
    ksp("com.google.dagger:hilt-android-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")

    // Room
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.11.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.57.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:2.57.2")
}

/* ────────────────────────────────────────────────────────────────────────────
   Jacoco 설정 (유닛 테스트용)
   - 테스트 task에 에이전트 확실히 부착
   - exec 파일을 표준/대체 경로 패턴으로 안전하게 수집
   - Provider/Lazy 방식으로 의존만 선언(조기 접근 방지)
   ──────────────────────────────────────────────────────────────────────────── */

tasks.withType<Test>().configureEach {
    // 테스트 완료 후 자동으로 리포트 생성
    finalizedBy("jacocoDebugUnitTestReport")

    // 일부 환경에서 커버리지 수집 누락 방지
    extensions.configure(JacocoTaskExtension::class) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register<JacocoReport>("jacocoDebugUnitTestReport") {
    dependsOn("testDebugUnitTest")

    // 실행 데이터(.exec) 수집: 표준 + 예외 경로들
    val execFiles = fileTree(layout.buildDirectory.asFile.get()) {
        include(
            "jacoco/testDebugUnitTest.exec",          // 표준
            "jacoco/test.exec",                       // 경우에 따라 생성될 수 있음
            "outputs/unit_test_code_coverage/**.exec",
            "**/jacoco-ut/*.exec"
        )
    }
    executionData( execFiles)

    // 클래스/소스 디렉토리
    val kotlinClasses = fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug"))
    val javaClasses = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes"))

    val excludes = listOf(
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "androidx/**", "**/*Test*.*", "**/*$*Companion*.*", "**/*$*WhenMappings*.*",
        // Hilt/DI 생성물
        "**/*_Factory.*", "**/*_Hilt*.*", "**/*_MembersInjector.*",
        "**/*_Provide*Factory.*", "**/*Hilt*.*"
    )

    classDirectories.setFrom(
        files(
            kotlinClasses.apply { exclude(excludes) },
            javaClasses.apply { exclude(excludes) }
        )
    )
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/jacocoDebugUnitTestReport/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoDebugUnitTestReport/report.xml"))
    }
}
