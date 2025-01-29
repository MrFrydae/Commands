plugins {
    java
    id("io.freefair.lombok") version "8.7.1" apply false
    id("java-library")
}

subprojects {
    apply {
        plugin("java")
        plugin("io.freefair.lombok")
        plugin("java-library")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        // Test Dependencies
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
        testImplementation("org.junit.platform:junit-platform-suite:1.10.1")
        testImplementation("org.mockito:mockito-core:5.7.0")

        // Normal Dependencies
        implementation("com.fasterxml.jackson.core:jackson-core:2.16.0")
        implementation("com.google.auto.service:auto-service-annotations:1.1.1")
        implementation("com.google.code.gson:gson:2.10.1")
        implementation("com.google.guava:guava:32.1.3-jre")
        implementation("com.googlecode.json-simple:json-simple:1.1.1")
        implementation("org.apache.commons:commons-csv:1.10.0")
        implementation("org.apache.commons:commons-lang3:3.14.0")
        implementation("org.jetbrains:annotations:24.1.0")
        implementation("org.reflections:reflections:0.10.2")
        implementation("org.slf4j:slf4j-api:2.0.9")

        // Runtime Dependencies
        runtimeOnly("ch.qos.logback:logback-classic:1.2.8")

        // Annotation Processors
        annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    }

    java {
        withSourcesJar()

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks {
        compileJava {
            options.compilerArgs.add("-parameters")
        }
    }
}

