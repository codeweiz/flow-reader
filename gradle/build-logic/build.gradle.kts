plugins {
    `kotlin-dsl`
}

repositories {
    maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
    maven { setUrl("https://maven.aliyun.com/repository/google") }
    maven { setUrl("https://maven.aliyun.com/repository/central") }
    gradlePluginPortal()
    google()
    mavenCentral()
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
