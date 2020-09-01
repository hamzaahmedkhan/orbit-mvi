/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    iosX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
                kotlin("stdlib-native")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(project(":orbit-2-test"))
                GroupedDependencies.testsImplementation.forEach { implementation(it) }
                runtimeOnly(ProjectDependencies.junitJupiterEngine)
            }
        }
//        iosX64Main {
//            dependencies {
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-native:1.3.9")
//            }
//        }
    }
}

// Fix lack of source code when publishing pure Kotlin projects
// See https://github.com/novoda/bintray-release/issues/262
//tasks.whenTaskAdded {
//    if (name == "generateSourcesJarForMavenPublication") {
//        this as Jar
//        from(sourceSets.main.get().allSource)
//    }
//}
