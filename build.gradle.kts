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

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.gradlePlugin.android)
        classpath(libs.gradlePlugin.kotlin)
        classpath(libs.gradlePlugin.dokka)
        classpath(libs.gradlePlugin.publish)
    }
}

allprojects {
    val isSnapshot = hasProperty("snapshot")
    if (isSnapshot) {
        val version = findProperty("VERSION_NAME")?.toString()
        if (version != null) {
            // add '-SNAPSHOT' suffix to an existing version when
            // it was requested to build a snapshot.
            setProperty("VERSION_NAME", "$version-SNAPSHOT")
        }
    }
    repositories {
        google()
        mavenCentral()
    }
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}
