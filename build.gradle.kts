import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml.Load

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta13"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.2.1"
}

group = "org.stablerpg.stablebestiary"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    implementation("fr.mrmicky:FastInv:3.1.1")

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("dev.aurelium:auraskills-api-bukkit:2.3.0")
    compileOnly("dev.aurelium:slate:1.1.13")

    compileOnly("com.zaxxer:HikariCP:6.3.0")
    compileOnly("com.h2database:h2:2.3.232")
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.5.3")
}

tasks {
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
    compileJava {
        options.release.set(21)
    }
    jar {
        enabled = false
    }
    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")
        minimize()
        relocate("fr.mrmicky.fastinv", "${project.group}.fastinv")
    }
    assemble {
        dependsOn(shadowJar)
    }
    paperweight {
        reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
    }
    paperPluginYaml {
        name.set(rootProject.name)
        main.set("${project.group}.${rootProject.name}")
        loader.set("${project.group}.BestiaryLoader")
        apiVersion.set("1.21")
        loader.set("STARTUP")
        author.set("ImNotStable")
        dependencies {
            server("PlaceholderAPI", Load.BEFORE, required = true, joinClasspath = true)
        }
    }
}
