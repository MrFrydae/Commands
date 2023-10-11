plugins {
    java
    id("io.freefair.lombok") version "8.3"
}

group = "dev.frydae"
version = "${property("jda_version")}-SNAPSHOT"

apply(from = "$rootDir/publishing.gradle")

dependencies {
    implementation("net.dv8tion:JDA:${property("jda_version")}") {
        exclude("opus-java")
    }

    api(project(":fcs-core"))
}

tasks {
    shadowJar {
        dependencies {
            include(project(":fcs-core"))
        }
    }
}