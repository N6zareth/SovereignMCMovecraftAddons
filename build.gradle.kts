plugins {
    id("java")
    id("io.github.0ffz.github-packages") version "1.2.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.sovereignmc"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly(files("libs/Movecraft_8.0.0_beta-5.jar"))
    compileOnly(files("libs/Movecraft-Combat_2.0.0_beta-6.jar"))
    implementation("net.kyori:adventure-api:4.21.0")
    implementation("net.kyori:adventure-platform-bukkit:4.4.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}


tasks {
    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }
    shadowJar {
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }
}