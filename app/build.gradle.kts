plugins {
    id("flow.android.application")
    alias(libs.plugins.ksp)
}

android {
    namespace = "io.flowreader.app"

    defaultConfig {
        applicationId = "io.flowreader.app"
        versionCode = 1
        versionName = "0.1.0"
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
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":core-common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.voyager)
    implementation(libs.androidx.documentfile)
    implementation(libs.material)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
