plugins {
    java
    id("io.freefair.lombok") version "8.3" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("java-library")
}

subprojects {
    apply {
        plugin("java")
        plugin("io.freefair.lombok")
        plugin("com.github.johnrengelman.shadow")
        plugin("java-library")
    }

    repositories {
        mavenCentral()
        google()

        maven {
            name = "frydae-repo"
            url = uri("https://repo.frydae.dev/repository/maven-snapshots/")
        }

        maven {
            name = "maven-dv8tion"
            url = uri("https://m2.dv8tion.net/releases")
        }
    }

    java {
        withSourcesJar()
        withJavadocJar()

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependencies {
        // Test Dependencies
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
        testImplementation("org.junit.platform:junit-platform-suite:1.9.1")
        testImplementation("org.mockito:mockito-core:5.5.0")

        // Normal Dependencies
        implementation("com.google.auto.service:auto-service-annotations:1.0.1")
        implementation("com.google.code.gson:gson:2.8.9")
        implementation("com.google.code.gson:gson:2.8.9")
        implementation("com.google.guava:guava:32.1.2-jre")
        implementation("com.googlecode.json-simple:json-simple:1.1.1")
        implementation("com.googlecode.json-simple:json-simple:1.1.1")
        implementation("org.apache.commons:commons-csv:1.5")
        implementation("org.apache.commons:commons-lang3:3.1")
        implementation("org.jetbrains:annotations:24.0.0")
        implementation("org.slf4j:slf4j-api:2.0.7")
        implementation("org.slf4j:slf4j-api:2.0.9")

        // Runtime Dependencies
        runtimeOnly("ch.qos.logback:logback-classic:1.2.8")

        // Annotation Processors
        annotationProcessor("com.google.auto.service:auto-service:1.0.1")
    }
}