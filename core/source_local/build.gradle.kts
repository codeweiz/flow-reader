plugins {
    id("flow.android.library")
}

android {
    namespace = "io.flowreader.core.source.local"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
}
