pluginManagement {
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
        kotlin("jvm") version "2.2.20"
    }
}
rootProject.name = "MChatV3"

include(":core")
include(":neoforge-1.21.1")