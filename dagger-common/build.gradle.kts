plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":api"))
    implementation(project(":processor-common"))

    implementation(libs.square.javaPoet)
    api(libs.xprocessing)
    implementation(libs.commons.text)
}