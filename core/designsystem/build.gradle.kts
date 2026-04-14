plugins {
    id("flow.android.library.compose")
}

android {
    namespace = "io.flowreader.core.designsystem"
}

dependencies {
    implementation(projects.core.model)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.bundles.compose)
    implementation(libs.material)
}
