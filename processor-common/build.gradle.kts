plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(project(":api"))

    implementation(libs.square.javaPoet)
    api(libs.xprocessing)
    implementation(libs.commons.text)
}