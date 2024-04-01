plugins {
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
}

android {
    namespace = "com.smmanager"
    compileSdk = Config.compileSdk

    defaultConfig {
        applicationId = Config.applicationId
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "SMMYa_URL",
                "\"http://84.201.175.97/\""
            )
            buildConfigField(
                "String",
                "SMMYa_URL_HOST",
                "\"84.201.175.97\""
            )
        }

        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "SMMYa_URL",
                "\"http://84.201.175.97/\""
            )
            buildConfigField(
                "String",
                "SMMYa_URL_HOST",
                "\"84.201.175.97\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(Dependencies.Lifecycle.core)
    implementation(Dependencies.Lifecycle.lifecycle)

    implementation(Dependencies.UI.appCompat)
    implementation(Dependencies.UI.material)
    implementation(Dependencies.UI.constraintLayout)
    implementation(Dependencies.UI.splashScreen)
    implementation(Dependencies.UI.swipeRefreshLayout)

    implementation(Dependencies.WebKit.webkit)

    testImplementation(Dependencies.Testing.junit)
    testImplementation(Dependencies.Testing.androidJunit)
    testImplementation(Dependencies.Testing.espresso)
}