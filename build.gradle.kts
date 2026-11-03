plugins {
    kotlin("jvm") version "2.3.20"
    id("org.jetbrains.intellij.platform") version "2.19.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.9"
}

group = "com.pcholt.kdocinlineimages"
version = "0.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        val localAndroidStudioHome = providers.gradleProperty("android.studio.home")
        if (localAndroidStudioHome.isPresent) {
            local(localAndroidStudioHome)
        } else {
            androidStudio(providers.gradleProperty("androidStudioVersion"))
        }
        bundledPlugin("org.jetbrains.kotlin")
    }
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.3")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.test {
    useJUnitPlatform()
}

intellijPlatform {
    pluginConfiguration {
        name = "KDoc Inline Images"
        ideaVersion {
            sinceBuild = "262"
            untilBuild = provider { null }
        }
    }
    buildSearchableOptions = false
}

kover {
    reports {
        filters {
            excludes {
                if (!providers.gradleProperty("includeIdeAdapters").isPresent) {
                    packages("com.pcholt.kdocinlineimages.ide")
                }
            }
        }
        verify {
            rule {
                minBound(80)
            }
        }
    }
}
