plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "inc.visor.voom.app"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "inc.visor.voom.app"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.fragment)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.osmdroid.android)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // Retrofit

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("androidx.datastore:datastore-preferences:1.2.0")
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.0.0")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("com.auth0.android:jwtdecode:2.0.2")
    implementation("androidx.gridlayout:gridlayout:1.0.0")

}