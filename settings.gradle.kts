pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.15"
}

val isCI = System.getenv("CI") != null

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        isUploadInBackground = !isCI
        publishAlways()

        capture {
            isTaskInputFiles = true
        }
    }
}

rootProject.name = "Commands"

include ("example-jda", "example-fabric", "fcs-jda", "fcs-core", "fcs-fabric")

project(":fcs-core").name = "fcs-core"
project(":fcs-jda").name = "fcs-jda"
project(":fcs-fabric").name = "fcs-fabric"