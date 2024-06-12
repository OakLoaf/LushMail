plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = "org.lushplugins"
version = "0.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") } // Spigot
    maven { url = uri("https://repo.lushplugins.org/releases/") } // LushLib
    maven { url = uri("https://repo.lushplugins.org/snapshots/") } // LushLib
}

dependencies {
    // Dependencies
    compileOnly("org.spigotmc:spigot-api:${findProperty("minecraftVersion")}-R0.1-SNAPSHOT")

    // Soft Dependencies
    compileOnly("net.luckperms:api:${findProperty("luckPermsVersion")}")

    // Libraries
    implementation("org.bstats:bstats-bukkit:${findProperty("bStatsVersion")}")
    implementation("com.zaxxer:HikariCP:${findProperty("hikariCPVersion")}")
    implementation("org.lushplugins:LushLib:${findProperty("lushLibVersion")}")
    implementation("com.mysql:mysql-connector-j:${findProperty("mysqlConnectorVersion")}")
    implementation("org.xerial:sqlite-jdbc:${findProperty("sqliteConnectorVersion")}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("org.bstats", "org.lushplugins.lushrewards.libraries.bstats")
        relocate("org.lushplugins.lushlib", "org.lushplugins.lushrewards.libraries.lushlib")
        relocate("com.mysql", "org.lushplugins.lushrewards.libraries.mysql")

        minimize {
            exclude(dependency("com.mysql:.*:.*"))
        }

        val folder = System.getenv("pluginFolder_1-20-6")
        if (folder != null) destinationDirectory.set(file(folder))
        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    processResources{
        expand(project.properties)

        inputs.property("version", rootProject.version)
        filesMatching("plugin.yml") {
            expand("version" to rootProject.version)
        }
    }
}