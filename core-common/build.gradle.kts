plugins {
    id("flow.android.library")
}

android {
    namespace = "io.flowreader.core.common"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.coroutines)
}
