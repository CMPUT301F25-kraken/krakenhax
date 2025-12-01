plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.secrets.gradle.plugin)
}

android {
    namespace = "com.kraken.krakenhax"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kraken.krakenhax"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true

    }
    secrets {
        // To add your Maps API key to this project:
        // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
        // 2. Add this line, where YOUR_API_KEY is your API key:
        //        MAPS_API_KEY=YOUR_API_KEY
        propertiesFileName = "secrets.properties"

        // A properties file containing default secret values. This file can be
        // checked in version control.
        defaultPropertiesFileName = "local.defaults.properties"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // For navigating between fragments (non-ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Firebase / storage / images
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.installations)
    implementation(libs.picasso)

    // QR codes
    implementation(libs.zxing.android.embedded)

    // Splash
    implementation(libs.core.splashscreen)

    // Maps / location
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // For QR Codes
    implementation(libs.zxing.android.embedded)
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.androidx.legacy.support.v4)

    // Test implementation
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.ext.junit)
    // UNIT TESTS
    testImplementation(libs.junit)

    // ANDROID TESTS (UI / instrumentation)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // FragmentScenario needs debugImplementation
    debugImplementation(libs.androidx.fragment.testing)
}

//// For generating javadocs
//tasks.register<Javadoc>("javadoc") {
//    // 1. Source files
//    source = android.sourceSets["main"].java.srcDirs
//        .fold(project.files().asFileTree) { tree, dir ->
//            tree.plus(fileTree(dir))
//        }
//
//    // 2. Classpath fix: Use the 'release' variant's compile classpath
//    // We need to wrap this in doFirst or use a provider to ensure variants are initialized
//    doFirst {
//        val releaseVariant = android.applicationVariants.first { it.name == "release" }
//        classpath = files(android.bootClasspath) + releaseVariant.javaCompileProvider.get().classpath
//    }
//
//    // 3. Destination
//    destinationDir = file("$buildDir/Javadocs")
//
//    // 4. Options
//    options.encoding = "UTF-8"
//    (options as StandardJavadocDocletOptions).memberLevel = JavadocMemberLevel.PUBLIC
//
//    // 5. Link to Android SDK documentation (Optional but helpful)
//    //(options as StandardJavadocDocletOptions).links("https://d.android.com/reference")
//
//    isFailOnError = false
//}
