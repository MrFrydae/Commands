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

    apply(from = uri("https://files.frydae.dev/gradle/common.gradle"))
}