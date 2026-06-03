plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.1"
}

group = "org.oauth_login_example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly(files("D:/Projects/MC_LinuxDo_OAuth/MC-LinuxDO-OAuth-Link/MC-LinuxDO-OAuth-Link/build/libs/MC-LinuxDO-OAuth-Link-1.0-SNAPSHOT.jar"))
}

tasks.shadowJar {
    archiveClassifier = ""
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
