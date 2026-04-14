plugins {
    id("flow.android.library")
}

android {
    namespace = "io.flowreader.core.data"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.database)
    implementation(projects.core.sourceEngine)
    implementation(projects.core.sourceLocal)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.documentfile)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.room.runtime)
}
