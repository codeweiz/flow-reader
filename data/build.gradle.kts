plugins {
    id("flow.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "io.flowreader.data"
}

ksp {
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core-common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.documentfile)

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    implementation(libs.kotlinx.coroutines.android)
}
