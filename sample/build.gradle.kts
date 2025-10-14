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

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.ksp)
}

val useKsp: Boolean = hasProperty("boringyuri.useKsp")

android {
    namespace = "boringyuri.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "boringyuri.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        buildConfigField("boolean", "NO_PLAY_SERVICES", "false")
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG_ONLY", "true")
        }
        release {
            buildConfigField("boolean", "DEBUG_ONLY", "false")

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

    buildFeatures {
        buildConfig = true
    }

    if (useKsp) {
        buildTypes.onEach { buildType ->
            if (productFlavors.isEmpty()) {
                sourceSets {
                    getByName("main")
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
                                "build/generated/ksp/${flavor.name}${buildType.name.replaceFirstCharToCapital()}/kotlin",
                                "build/generated/ksp/${flavor.name}${buildType.name.replaceFirstCharToCapital()}/java",
                            )
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.viewModel)
    implementation(libs.androidx.appCompat)
    implementation(libs.androidx.constraintLayout)

    // code generators
    implementation(project(":api"))
    // implementation("com.github.anton-novikau:boringyuri-api:${findProperty("VERSION_NAME")}")
    if (useKsp) {
        ksp(project(":processor-ksp"))
        // ksp("com.github.anton-novikau:boringyuri-processor-ksp:${findProperty("VERSION_NAME")}")
    } else {
        kapt(project(":processor"))
        // kapt("com.github.anton-novikau:boringyuri-processor:${findProperty("VERSION_NAME")}")
    }


    // unit tests
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
}

if (useKsp) {
    ksp {
        arg("boringyuri.type_adapter_factory", "boringyuri.sample.data.adapter.factory.TypeAdapterFactory")
    }
} else {
    kapt {
        useBuildCache = true
        javacOptions {
            option("-Xmaxerrs", 1000.toString()) // max count of AP errors
        }
        arguments {
            arg(
                "boringyuri.type_adapter_factory",
                "boringyuri.sample.data.adapter.factory.TypeAdapterFactory"
            )
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11) // in order to compile Kotlin to java 11 bytecode
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

fun String.replaceFirstCharToCapital() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}
