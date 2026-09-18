plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "app.morphe.extension.sharedyoutube.library"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    compileOnly(libs.annotation)

    implementation(libs.gson)
    implementation(project(":extensions:shared:library"))
}
