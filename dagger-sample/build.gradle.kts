/*
 * Copyright 2020 Anton Novikau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
}

val useKsp: Boolean = hasProperty("boringyuri.useKsp")

android {
    namespace = "boringyuri.dagger.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTools.get()

    defaultConfig {
        applicationId = "boringyuri.dagger.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }
    if (useKsp) {
        buildTypes.onEach { buildType ->
            if (productFlavors.isEmpty()) {
                sourceSets {

                    getByName(buildType.name)
                        .kotlin
                        .srcDirs(
                            "build/generated/ksp/${buildType.name}/kotlin",
                            "build/generated/ksp/${buildType.name}/java",
                        )
                }
            } else {
                productFlavors.onEach { flavor ->
                    sourceSets {
                        getByName("main")
                            .kotlin
                            .srcDirs(
                                "build/generated/ksp/${flavor.name}${buildType.name.capitalize()}/kotlin",
                                "build/generated/ksp/${flavor.name}${buildType.name.capitalize()}/java",
                            )
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.androidx.appCompat)
    implementation(libs.androidx.constraintLayout)

    // code generators
    implementation(project(":api"))
    // implementation("com.github.anton-novikau:boringyuri-api:${findProperty("VERSION_NAME")}")

    if (useKsp) {
        ksp(project(":processor-ksp"))
        // ksp("com.github.anton-novikau:boringyuri-processor-ksp:${findProperty("VERSION_NAME")}")
        ksp(project(":dagger-ksp"))
        // ksp("com.github.anton-novikau:boringyuri-dagger-ksp:${findProperty("VERSION_NAME")}")
    } else {
        kapt(project(":processor"))
        // kapt("com.github.anton-novikau:boringyuri-processor:${findProperty("VERSION_NAME")}")
        kapt(project(":dagger"))
        // kapt("com.github.anton-novikau:boringyuri-dagger:${findProperty("VERSION_NAME")}")
    }

    implementation(libs.bundles.dagger)
    kapt(libs.bundles.dagger.compiler)

    // unit tests
    testImplementation(libs.junit)
}

if (useKsp) {
    ksp {
        arg(
            "boringyuri.type_adapter_factory",
            "boringyuri.dagger.sample.data.adapter.TypeAdapterFactory"
        )
        arg("boringyuri.dagger.module", "boringyuri.dagger.sample.di.BoringYuriModule")
    }
} else {
    kapt {
        arguments {
            arg(
                "boringyuri.type_adapter_factory",
                "boringyuri.dagger.sample.data.adapter.TypeAdapterFactory"
            )
            arg("boringyuri.dagger.module", "boringyuri.dagger.sample.di.BoringYuriModule")
        }
    }
}

kapt {
    useBuildCache = true
    javacOptions {
        option("-Xmaxerrs", 1000) // max count of AP errors
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString() // in order to compile Kotlin to java 11 bytecode
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}
