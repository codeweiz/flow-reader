plugins {
    id("flow.android.library")
}

android {
    namespace = "io.flowreader.core.source.engine"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
}
