plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version("8.3.3")
}

group = "org.lushplugins"
version = "0.2.7"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://repo.lushplugins.org/releases/") // LushLib
    maven("https://repo.lushplugins.org/snapshots/") // LushLib
}

dependencies {
    // Dependencies
    compileOnly("org.spigotmc:spigot-api:${findProperty("minecraftVersion")}-R0.1-SNAPSHOT")
    compileOnly("com.mysql:mysql-connector-j:${findProperty("mysqlConnectorVersion")}")
    compileOnly("org.xerial:sqlite-jdbc:${findProperty("sqliteConnectorVersion")}")

    // Soft Dependencies
    compileOnly("net.luckperms:api:${findProperty("luckPermsVersion")}")

    // Libraries
    implementation("org.bstats:bstats-bukkit:${findProperty("bStatsVersion")}")
    implementation("com.zaxxer:HikariCP:${findProperty("hikariCPVersion")}")
    implementation("org.lushplugins:LushLib:${findProperty("lushLibVersion")}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))

    withSourcesJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("org.bstats", "org.lushplugins.lushrewards.libraries.bstats")
        relocate("org.lushplugins.lushlib", "org.lushplugins.lushrewards.libraries.lushlib")

        minimize {
            exclude(dependency("com.mysql:.*:.*"))
        }

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

publishing {
    publishing {
        repositories {
            maven {
                name = "lushReleases"
                url = uri("https://repo.lushplugins.org/releases")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }

            maven {
                name = "lushSnapshots"
                url = uri("https://repo.lushplugins.org/snapshots")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = rootProject.name
                version = rootProject.version.toString()
                from(project.components["java"])
            }
        }
    }
}