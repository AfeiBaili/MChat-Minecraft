pluginManagement {
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
        kotlin("jvm") version "2.0.0"
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven { url = uri("https://maven.neoforged.net/releases") }
        maven { url = uri("https://maven.minecraftforge.net/") }
        maven { url = uri("https://maven.parchmentmc.org") }
    }
}
rootProject.name = "MChatV3"

include(":core")
include(":neoforge-1.21.1")
include(":forge-1.20.1")
include(":forge-1.19.2")