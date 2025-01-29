plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.frydae"
version = "${property("jda_version")}"

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