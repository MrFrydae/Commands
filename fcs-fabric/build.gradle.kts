plugins {
    java
    id ("fabric-loom") version "1.4-SNAPSHOT"
    id ("maven-publish")
}

group = property("maven_group")!!
version = property("mod_version")!!

apply (from = "$rootDir/common.gradle")
apply (from = "$rootDir/publishing.gradle")

dependencies {
    minecraft ("com.mojang:minecraft:${property("minecraft_version")}")
    mappings ("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation ("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modImplementation ("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand(mutableMapOf(
                    "version" to project.version
            ))
        }
    }

    jar {
        from("LICENSE")
    }
}

java {
    withSourcesJar()
}