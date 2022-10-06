plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":processor-common"))
    implementation(libs.ksp.api)
}