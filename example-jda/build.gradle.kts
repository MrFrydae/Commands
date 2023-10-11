plugins {
    id("application")
}

application {
    mainClass = "dev.frydae.bot.DiscordBot"
}

group = "dev.frydae"
version = "0.0.1"

dependencies {
    implementation(project(":fcs-jda"))
    annotationProcessor(project(":fcs-jda"))

    implementation("net.dv8tion:JDA:${property("jda_version")}") {
        exclude("opus-java")
    }
}