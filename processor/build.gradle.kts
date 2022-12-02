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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

val fatJarMembers: Set<String> = setOf(
    "processor-common",
    "processor-common-apt",
    "processor-steps",
)

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":api"))
    fatJarMembers.forEach { projectName ->
        compileOnly(project(":$projectName"))
    }

    //noinspection AnnotationProcessorOnCompilePath
    compileOnly(libs.google.auto.service)
    kapt(libs.google.auto.service)

    implementation(libs.square.javaPoet)
    implementation(libs.androidx.room.xprocessing)
    implementation(libs.commons.text)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.jar.configure {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(
        configurations.compileClasspath.get()
            .filter { it.extension == "jar" && it.nameWithoutExtension in fatJarMembers }
            .map {
                zipTree(it)
            }
    )
}

val fatSourcesJar = tasks.register<Jar>("fatSourcesJar") {
    archiveClassifier.set("fatSources")
    // TODO: configure assembling fat jar sources

//    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//
//    from(sourceSets.main.get().java.srcDirs.single())
//    fatJarMembers.forEach { projectName ->
//        from(project(":$projectName").sourceSets.main.get().java.srcDirs.single())
//    }
}

val fatJavadocJar = tasks.register<Jar>("fatJavadocJar") {
    archiveClassifier.set("fatJavadoc")
    // TODO: configure assembling fat jar javadocs
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString() // in order to compile Kotlin to java 11 bytecode
    }
}

publishing {
    publications {
        create<MavenPublication>("fatJar") {
            from(components.getByName("java"))

            artifact(fatSourcesJar)
            artifact(fatJavadocJar)

            groupId = project.findStringProperty("GROUP")
            artifactId = project.findStringProperty("POM_ARTIFACT_ID")
            version = project.findStringProperty("VERSION_NAME")

            pom {
                name.set(project.findStringProperty("POM_NAME"))
                description.set(project.findStringProperty("POM_DESCRIPTION"))
                url.set(project.findStringProperty("POM_URL"))
                inceptionYear.set(project.findStringProperty("POM_INCEPTION_YEAR"))
                scm {
                    url.set(project.findStringProperty("POM_SCM_URL"))
                    connection.set(project.findStringProperty("POM_SCM_CONNECTION"))
                    developerConnection.set(project.findStringProperty("POM_SCM_DEV_CONNECTION"))
                }
                licenses {
                    license {
                        name.set(project.findStringProperty("POM_LICENCE_NAME"))
                        url.set(project.findStringProperty("POM_LICENSE_URL"))
                        distribution.set(project.findStringProperty("POM_LICENSE_DIST"))
                    }
                }
                developers {
                    developer {
                        id.set(project.findStringProperty("POM_DEVELOPER_ID"))
                        name.set(project.findStringProperty("POM_DEVELOPER_NAME"))
                        url.set(project.findStringProperty("POM_DEVELOPER_URL"))
                    }
                }
            }
        }
    }
}

fun Project.findStringProperty(name: String) = requireNotNull(findProperty(name)).toString()
