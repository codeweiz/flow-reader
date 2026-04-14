plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "flow.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "flow.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("kotlinJvm") {
            id = "flow.kotlin.jvm"
            implementationClass = "KotlinJvmConventionPlugin"
        }
    }
}
