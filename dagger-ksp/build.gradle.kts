plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":api"))
    implementation(project(":dagger-common"))
    implementation(project(":processor-ksp"))
    implementation(project(":processor-common"))

    implementation(libs.ksp.api)
}