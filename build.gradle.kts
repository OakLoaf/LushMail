plugins {
    java
    `maven-publish`
    id("io.github.goooler.shadow") version("8.1.7")
}

group = "org.lushplugins"
version = "0.2.2"

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

tasks {

    shadowJar {
        relocate("org.bstats", "org.lushplugins.lushrewards.libraries.bstats")
        relocate("org.lushplugins.lushlib", "org.lushplugins.lushrewards.libraries.lushlib")
        relocate("com.mysql", "org.lushplugins.lushrewards.libraries.mysql")

        minimize {
            exclude(dependency("com.mysql:.*:.*"))
        }

        val folder = System.getenv("pluginFolder")
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

allprojects {
    apply(plugin = "java")

    group = rootProject.group
    version = rootProject.version

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
    }
}