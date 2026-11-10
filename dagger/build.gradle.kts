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
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SourcesJar
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.dokka)
    alias(libs.plugins.maven.publish)
}

val fatJarMembers: Set<String> = setOf(
    "processor-dagger-steps",
)

fatJarMembers.forEach {
    evaluationDependsOn(":$it")
}

dependencies {
    compileOnly(project(":api"))
    compileOnly(project(":processor-common"))
    compileOnly(project(":processor-common-apt"))
    compileOnly(project(":processor-dagger-steps"))

    implementation(project(":processor"))

    //noinspection AnnotationProcessorOnCompilePath
    compileOnly(libs.google.auto.service)
    kapt(libs.google.auto.service)

    implementation(libs.square.javaPoet)
    implementation(libs.androidx.room.xprocessing)
    // XProcessing brings an older kotlin-metadata-jvm transitively which is unable to read
    // metadata of classes compiled with newer Kotlin versions, so it must be aligned with Kotlin.
    implementation(libs.kotlin.metadataJvm)
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

tasks.withType(Jar::class).configureEach {
    if (name == "sourcesJar") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        fatJarMembers.forEach {
            from(
                project(":$it").sourceSets.getByName("main").java.srcDirs
            )
        }
    }
}

dokka {
    dokkaSourceSets.configureEach {
        fatJarMembers.forEach {
            sourceRoots.from(
                project(":$it").sourceSets.getByName("main").java.srcDirs
            )
        }
    }
}

tasks.matching { it.name.startsWith("dokkaGenerate") }.configureEach {
    mustRunAfter(tasks["kaptKotlin"])
}


tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11) // in order to compile Kotlin to java 11 bytecode
    }
}

mavenPublishing {
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = SourcesJar.Sources(),
        )
    )
}
