plugins {
    id("flow.android.library")
    id("flow.android.room")
}

android {
    namespace = "io.flowreader.core.database"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
}
